package co.aurasphere.echo.rasa.publisher.model.rasa.nlu;

import java.util.List;

public class EntitySynonym {
    
    private String value;
    
    private List<String> synonyms;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }

}