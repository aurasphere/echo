package co.aurasphere.echo.rasa.publisher.model.rasa.nlu;

import java.util.Set;

public class LookupTable {
    
    private String name;
    
    private Set<String> elements;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getElements() {
        return elements;
    }

    public void setElements(Set<String> elements) {
        this.elements = elements;
    }
    

}
