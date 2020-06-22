package co.aurasphere.echo.rasa.fulfillment.model;

public class Event {

    private String event;

    private String name;
    
    private String value;
    
    private long timestamp = 0;

    public Event(String event, String name, String value) {
        this.event = event;
        this.name = name;
        this.value = value;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
}