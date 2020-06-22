package co.aurasphere.echo.rasa.publisher;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.ByteSource;
import com.google.gson.Gson;

import co.aurasphere.echo.rasa.publisher.model.indie.Indie;
import co.aurasphere.echo.rasa.publisher.model.indie.Intent;
import co.aurasphere.echo.rasa.publisher.model.indie.Parameter;
import co.aurasphere.echo.rasa.publisher.model.indie.Part;
import co.aurasphere.echo.rasa.publisher.model.indie.Utterance;
import co.aurasphere.echo.rasa.publisher.model.rasa.nlu.CommonExample;
import co.aurasphere.echo.rasa.publisher.model.rasa.nlu.Entity;
import co.aurasphere.echo.rasa.publisher.model.rasa.nlu.EntitySynonym;
import co.aurasphere.echo.rasa.publisher.model.rasa.nlu.LookupTable;
import co.aurasphere.echo.rasa.publisher.model.rasa.nlu.NluData;
import co.aurasphere.echo.rasa.publisher.strategy.FilePublishStrategy;
import co.aurasphere.echo.rasa.publisher.strategy.PublishStrategy;
import co.aurasphere.echo.rasa.publisher.strategy.RestPublishStrategy;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class EchoRasaPublisher {

    /**
     * Used for mapping slot types. Contains constants.
     */
    private static Map<String, String> nativeParameterMap = new HashMap<>();

    /**
     * Contains sample parameters for each data type (primitive and custom).
     * Populated during generation of NLU file and used for building stories.
     * Sample content: @sys.number -> 1, slot-11 -> "San Francisco"
     * 
     */
    private static Map<String, String> sampleParameterMap = new HashMap<>();

    private static PublishStrategy publishStrategy;

    /**
     * Populated during NLU file generation.
     */
    private static Set<Part> slotsSet = new HashSet<>();

    /**
     * Populated during NLU, used during Domain.
     */
    private static Set<String> informIntents = new HashSet<>();

    /**
     * Custom actions for mappning duckling slots. Populated during Stories, used during domain.
     */
    private static Set<String> ducklingSlotsMappingActions = new HashSet<>();

    // Native entity types.
    static {
        sampleParameterMap.put("@sys.number", "over $100");
        sampleParameterMap.put("@sys.geo-state", "California");
        sampleParameterMap.put("@sys.date-time", "from May 1 2019");

        nativeParameterMap.put("@sys.number", "text");
        nativeParameterMap.put("@sys.geo-state", "text");
        nativeParameterMap.put("@sys.date-time", "unfeaturized");
    }

    public static void main(String[] args) throws Exception {
        // Get indie file from args.
        ArgumentParser parser = ArgumentParsers.newFor("echo-rasa-publisher")
            .build()
            .defaultHelp(true)
            .description("Publish a InDie file into a Rasa bot through training.");
        parser.addArgument("-i", "--indie")
            .required(true)
            .help("The InDie file to use for training.");
        parser.addArgument("-o", "--output-directory")
            .help("Output directory. Used only for debug (file dump).");
        parser.addArgument("-a", "--action-server-endpoint")
            .setDefault("http://localhost:5055/webhook")
            .help("Endpoint of action (fulfillment) server.");
        parser.addArgument("-r", "--rasa-server-address")
            .setDefault("http://localhost:5005")
            .help("Address of the Rasa bot server for training.");
        parser.addArgument("-d", "--duckling-server-address")
            .setDefault("http://localhost:8000")
            .help("Address of the Duckling server for entity recognition.");
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        // Select publish strategy.
        String outputDirectory = ns.getString("output_directory");
        if (outputDirectory != null) {
            publishStrategy = new FilePublishStrategy(outputDirectory);
        } else {
            String rasaServerAddress = ns.getString("rasa_server_address");
            publishStrategy = new RestPublishStrategy(rasaServerAddress);
        }

        // Parse indie file with Gson.
        String indieFile = ns.getString("indie");
        Gson gson = new Gson();
        String indieJson = new String(Files.readAllBytes(Paths.get(indieFile)));
        Indie parsedIndie = gson.fromJson(indieJson, Indie.class);

        // Convert indie file to Rasa files.
        Map<String, Map<String, String>> endpoints = createEndpoints(ns.getString("action_server_endpoint"));
        Map<String, NluData> nlu = createNlu(parsedIndie);
        String stories = createStories(parsedIndie);
        Map<String, Object> domain = createDomain(parsedIndie);
        String config = createConfig(ns.getString("duckling_server_address"));

        // Publish to bot.
        publishStrategy.publish(endpoints, nlu, domain, stories, config);
    }

    private static String createConfig(String ducklingServerAddress) throws IOException {
        InputStream configFile = EchoRasaPublisher.class
            .getClassLoader()
            .getResourceAsStream("config-template.yml");
        ByteSource byteSource = new ByteSource() {
            @Override
            public InputStream openStream() throws IOException {
                return configFile;
            }
        };
        String configFileContent = byteSource.asCharSource(Charsets.UTF_8)
            .read();
        
        // Fills in the address into the template.
        return configFileContent.replace("${duckling_server_address}", ducklingServerAddress);
    }

    // DATA

    private static Map<String, Map<String, String>> createEndpoints(String endpoint) {
        Map<String, String> actionEndpoint = Collections.singletonMap("url", endpoint);
        Map<String, Map<String, String>> yamlContent = Collections.singletonMap("action_endpoint", actionEndpoint);
        return yamlContent;
    }

    private static Map<String, NluData> createNlu(Indie parsedIndie) {
        NluData nluContent = new NluData();

        List<CommonExample> commonExamples = parsedIndie.getIntents()
            .stream()
            .map(EchoRasaPublisher::intentToCommonExamples)
            .flatMap(List::stream)
            .collect(Collectors.toList());
        nluContent.setCommonExamples(commonExamples);

        List<LookupTable> lookupTables = new ArrayList<>();
        List<EntitySynonym> entitySynonyms = new ArrayList<>();

        // Creates a new lookup table for each custom slot value.
        for (Part part : slotsSet) {
            LookupTable table = new LookupTable();
            table.setName(part.getAlias());
            Optional<co.aurasphere.echo.rasa.publisher.model.indie.Entity> customEntity = parsedIndie.getCustomEntities()
                .values()
                .stream()
                .filter(entity -> part.getEntityType()
                    .equals(entity.getEntityType()))
                .findFirst();

            // Native type, let's just continue.
            if (!customEntity.isPresent()) {
                continue;
            }
            Map<String, List<String>> dictionary = customEntity.get()
                .getDictionary();
            table.setElements(dictionary.keySet());
            lookupTables.add(table);

            // Samples the parameter for the stories later.
            sampleParameterMap.put(part.getAlias(), table.getElements()
                .stream()
                .findFirst()
                .get());

            // Entity synonyms.
            Set<Entry<String, List<String>>> dictionarySynonyms = dictionary.entrySet();
            for (Entry<String, List<String>> entry : dictionarySynonyms) {
                List<String> dictionaryValue = entry.getValue();
                if (dictionaryValue.size() > 1) {
                    EntitySynonym synonym = new EntitySynonym();
                    synonym.setValue(entry.getKey());
                    synonym.setSynonyms(dictionaryValue);
                    entitySynonyms.add(synonym);
                }
            }

        }
        nluContent.setLookupTables(lookupTables);
        nluContent.setEntitySynonyms(entitySynonyms);

        return Collections.singletonMap("rasa_nlu_data", nluContent);
    }

    private static String createStories(Indie parsedIndie) {
        StringBuilder fileContent = new StringBuilder();
        List<Intent> intents = parsedIndie.getIntents();
        Gson gson = new Gson();
        for (Intent intent : intents) {
            Set<Parameter> mandatoryParameters = intent.getParameters()
                .stream()
                .filter(Parameter::getMandatory)
                .collect(Collectors.toSet());

            // Needed to add custom mapping for duckling
            Set<Parameter> ducklingParameters = intent.getParameters()
                .stream()
                .filter(p -> p.getEntityType()
                    .equals("@sys.date-time"))
                .collect(Collectors.toSet());

            Set<Parameter> totalParameters = Sets.union(mandatoryParameters, ducklingParameters);
            Set<Set<Parameter>> totalParametersPowerSet = Sets.powerSet(totalParameters);

            // We build a story for each mandatory parameters combination.
            for (Set<Parameter> availableParametersSet : totalParametersPowerSet) {
                // Missing parameters.
                Set<Parameter> missingParameters = Sets.difference(mandatoryParameters, availableParametersSet);

                // Maps input parameters to samples.
                Function<Parameter, String> sampleParametersMappingFunction = p -> {
                    String parameterEntityType = p.getEntityType();
                    String parameterId = p.getId();
                    String parameterSample = sampleParameterMap.get(parameterId);

                    // If it's a duckling type, return accordingly. At the moment only date is handled.
                    if (ducklingParameters.contains(p)) {
                        return "2020-07-01T00:00:00.000+02:00";
                    }

                    // If no sample was returned it means it's a custom type. Lookup by type.
                    if (parameterSample == null) {
                        return sampleParameterMap.get(parameterEntityType);
                    }
                    return parameterSample;
                };

                Function<Parameter, String> idParametersMappingFunctions = p -> {
                    // Handles duckling parameters. For now only date is supported.
                    if (ducklingParameters.contains(p)) {
                        return "time";
                    }
                    return p.getId();
                };

                // Gets a sample for each parameter.
                Map<String, String> inputSampleParametersMap = availableParametersSet.stream()
                    .collect(Collectors.toMap(idParametersMappingFunctions, sampleParametersMappingFunction));

                // Story input.
                String inputParametersJson = gson.toJson(inputSampleParametersMap);

                // Story name.
                fileContent.append("## ")
                    .append(intent.getId())
                    .append(" with input params ")
                    .append(inputParametersJson)
                    .append("\n");

                // Starting intent.
                fileContent.append("* ")
                    .append(intent.getId());
                if (!availableParametersSet.isEmpty()) {
                    fileContent.append(inputParametersJson);
                }
                fileContent.append("\n");

                // Fills missing slots.
                for (Parameter parameter : missingParameters) {
                    // Sample parameter.
                    String parameterId = parameter.getId();
                    Map<String, String> sampleInputMap = Collections.singletonMap(parameterId, sampleParametersMappingFunction.apply(parameter));
                    fileContent.append("\t- utter_ask_")
                        .append(intent.getId())
                        .append("_")
                        .append(parameterId)
                        .append("\n");
                    fileContent.append("* inform_")
                        .append(intent.getId())
                        .append(gson.toJson(sampleInputMap))
                        .append("\n");
                }

                // If we're using some duckling parameters, add the custom mappings.
                Set<Parameter> ducklingSlots = Sets.intersection(ducklingParameters, availableParametersSet);
                for (Parameter d : ducklingSlots) {
                    // Only time supported for now.
                    String intentName = "action_map_slots_time_" + d.getId();
                    fileContent.append("\t- " + intentName)
                        .append("\n");
                    ducklingSlotsMappingActions.add(intentName);
                }

                // All slots filled, send to server.
                fileContent.append("\t- action_send_server_")
                    .append(intent.getId())
                    .append("\n\n");
            }
        }
        return fileContent.toString();
    }

    private static Map<String, ? super Object> createDomain(Indie parsedIndie) {
        Map<String, ? super Object> yamlContent = new HashMap<>();

        // Intents.
        List<String> baseIntents = parsedIndie.getIntents()
            .stream()
            .map(Intent::getId)
            .collect(Collectors.toList());
        List<String> intents = new ArrayList<>(baseIntents);
        intents.addAll(informIntents);
        yamlContent.put("intents", intents);

        // Entities.
        List<String> entities = slotsSet.stream()
            .map(Part::getAlias)
            .collect(Collectors.toList());
        // Adds duckling custom entities.
        entities.add("time");

        yamlContent.put("entities", entities);

        // Actions.
        List<String> actions = baseIntents.stream()
            .map("action_send_server_"::concat)
            .collect(Collectors.toList());
        actions.addAll(ducklingSlotsMappingActions);
        yamlContent.put("actions", actions);

        // Slots.
        Map<String, Map<String, String>> slots = new HashMap<>();
        for (Part p : slotsSet) {
            Map<String, String> slot = new HashMap<>();

            // Check if the type is native or custom.
            String slotType = nativeParameterMap.get(p.getEntityType());
            if (slotType == null) {
                // Custom types are mapped to text.
                slotType = "text";
            }
            slot.put("type", slotType);
            slots.put(p.getAlias(), slot);
        }

        // Adds duckling custom slots.
        Map<String, String> timeSlot = Collections.singletonMap("type", "unfeaturized");
        slots.put("time", timeSlot);

        yamlContent.put("slots", slots);

        // Responses
        Map<String, List<Map<String, String>>> responses = new HashMap<>();
        parsedIndie.getIntents()
            .stream()
            .map(EchoRasaPublisher::intentToResponse)
            .forEach(responses::putAll);
        yamlContent.put("responses", responses);
        return yamlContent;
    }

    // MAPPINGS

    private static Map<String, List<Map<String, String>>> intentToResponse(Intent intent) {
        Map<String, List<Map<String, String>>> response = new HashMap<>();

        String responseTemplate = intent.getResponseTemplate();
        List<Map<String, String>> responseContent = Collections.singletonList(Collections.singletonMap("text", responseTemplate));
        response.put("utter_" + intent.getId(), responseContent);

        // Maps the utterances to ask parameters.
        for (Parameter p : intent.getParameters()) {
            response.put("utter_ask_" + intent.getId() + "_" + p.getId(), Collections.singletonList(Collections.singletonMap("text", "What " + p.getFriendlyName() + "?")));
        }

        return response;
    }

    private static List<CommonExample> intentToCommonExamples(Intent intent) {
        List<CommonExample> examples = new ArrayList<>();
        for (Utterance u : intent.getUtterances()) {
            CommonExample example = utterancePartsToCommonExample(u.getParts(), intent.getId());
            examples.add(example);
        }

        // Do the same for followups.
        for (Utterance u : intent.getFollowupUtterances()) {
            String intentId = "inform_" + intent.getId();
            CommonExample example = utterancePartsToCommonExample(u.getParts(), intentId);
            informIntents.add(example.getIntent());
            examples.add(example);
        }

        return examples;

    }

    private static CommonExample utterancePartsToCommonExample(List<Part> utteranceParts, String intentName) {
        CommonExample example = new CommonExample();
        String text = "";

        // Joins all the parts.
        for (Part p : utteranceParts) {
            String partText = p.getText();
            text += partText;

            // Part represents an entity.
            if (p.getEntityType() != null) {
                Entity e = new Entity();
                e.setStart(text.indexOf(partText));
                e.setEnd(e.getStart() + partText.length());
                e.setEntity(p.getAlias());
                e.setValue(partText);
                List<Entity> entities = example.getEntities();
                if (entities == null) {
                    entities = new ArrayList<>();
                    example.setEntities(entities);
                }

                slotsSet.add(p);
                entities.add(e);
            }
        }
        example.setText(text);
        example.setIntent(intentName);
        return example;
    }
    
}