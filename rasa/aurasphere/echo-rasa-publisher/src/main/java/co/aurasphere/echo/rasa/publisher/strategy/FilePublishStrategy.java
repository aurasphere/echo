package co.aurasphere.echo.rasa.publisher.strategy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import com.google.gson.Gson;

import co.aurasphere.echo.rasa.publisher.model.rasa.nlu.NluData;

public class FilePublishStrategy implements PublishStrategy {

    private String outputDirectory;

    private Gson gson = new Gson();

    public FilePublishStrategy(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void publish(Map<String, Map<String, String>> endpoints, Map<String, NluData> nlu, Map<String, Object> domain, String stories, String config) throws IOException {

        writeYaml("endpoints.yml", endpoints);
        writeYaml("domain.yml", domain);

        // Writes the stories.
        File storiesFile = new File(outputDirectory, "stories.md");
        FileWriter storiesWriter = new FileWriter(storiesFile);
        storiesWriter.write(stories);
        storiesWriter.close();

        // Writes NLU
        String nluJson = gson.toJson(nlu);
        File nluFile = new File(outputDirectory, "nlu.json");
        FileWriter nluWriter = new FileWriter(nluFile);
        nluWriter.write(nluJson);
        nluWriter.close();

        // Writes config.
        File configFile = new File(outputDirectory, "config.yml");
        FileWriter configWriter = new FileWriter(configFile);
        configWriter.write(config);
        configWriter.close();
//        InputStream configStream = this.getClass()
//            .getClassLoader()
//            .getResourceAsStream("config.yml");
//        Files.copy(configStream, Paths.get(outputDirectory, "config.yml"), StandardCopyOption.REPLACE_EXISTING);
    }

    private void writeYaml(String fileName, Object content) throws IOException {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        File output = new File(outputDirectory, fileName);
        FileWriter writer = new FileWriter(output);
        yaml.dump(content, writer);
    }

}
