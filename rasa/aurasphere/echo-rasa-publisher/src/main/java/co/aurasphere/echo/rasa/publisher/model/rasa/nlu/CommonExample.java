package co.aurasphere.echo.rasa.publisher.model.rasa.nlu;

import java.util.List;

public class CommonExample {

    private String text;
    
    private List<Entity> entities;
    
    private String intent;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }
}