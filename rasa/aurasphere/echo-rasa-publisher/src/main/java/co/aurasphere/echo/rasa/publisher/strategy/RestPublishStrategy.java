package co.aurasphere.echo.rasa.publisher.strategy;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import com.google.gson.Gson;

import co.aurasphere.echo.rasa.publisher.model.rasa.nlu.NluData;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class RestPublishStrategy implements PublishStrategy {

    private String rasaServerAddress;
    private Gson gson = new Gson();

    public RestPublishStrategy(String rasaServerAddress) {
        this.rasaServerAddress = rasaServerAddress;
        Unirest.config()
            .connectTimeout(0)
            .socketTimeout(0);
    }

    @Override
    public void publish(Map<String, Map<String, String>> endpoints, Map<String, NluData> nlu, Map<String, Object> domain, String stories, String config) throws Exception {

//        InputStream configFile = this.getClass()
//            .getClassLoader()
//            .getResourceAsStream("config.yml");
//        ByteSource byteSource = new ByteSource() {
//            @Override
//            public InputStream openStream() throws IOException {
//                return configFile;
//            }
//        };
//        String configFileContent = byteSource.asCharSource(Charsets.UTF_8)
//            .read();

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("domain", yamlAsString(domain));
        requestBody.put("nlu", gson.toJson(nlu));
        requestBody.put("stories", stories);
        requestBody.put("config", config);

        String requestJson = gson.toJson(requestBody);
        System.out.println("Training server request: " + requestJson);

        HttpResponse<String> response = Unirest.post(rasaServerAddress + "/model/train")
            .header("Content-type", "application/json")
            .body(requestJson)
            .asString();
        
        int status = response.getStatus();
        System.out.println("Training server response: " + status);
        if (status != 200) {
            System.out.println("Message: " + response.getBody());            
        }
    }

    private String yamlAsString(Object content) throws IOException {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        StringWriter writer = new StringWriter();
        yaml.dump(content, writer);
        return writer.toString();
    }
}