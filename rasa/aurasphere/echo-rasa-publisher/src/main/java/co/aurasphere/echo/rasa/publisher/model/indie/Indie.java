package co.aurasphere.echo.rasa.publisher.model.indie;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class Indie {

    private String description;

    @SerializedName("source_indie_file")
    private String sourceIndieFile;

    @SerializedName("custom_entities")
    private Map<String, Entity> customEntities;

    private List<Intent> intents;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSourceIndieFile() {
        return sourceIndieFile;
    }

    public void setSourceIndieFile(String sourceIndieFile) {
        this.sourceIndieFile = sourceIndieFile;
    }

    public Map<String, Entity> getCustomEntities() {
        return customEntities;
    }

    public void setCustomEntities(Map<String, Entity> customEntities) {
        this.customEntities = customEntities;
    }

    public List<Intent> getIntents() {
        return intents;
    }

    public void setIntents(List<Intent> intents) {
        this.intents = intents;
    }

}