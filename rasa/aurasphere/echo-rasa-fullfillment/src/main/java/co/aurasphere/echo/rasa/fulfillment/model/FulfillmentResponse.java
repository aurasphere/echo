package co.aurasphere.echo.rasa.fulfillment.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FulfillmentResponse {
    
    private List<Response> responses;
    
    private List<Event> events;
    
    public FulfillmentResponse() {
        this.responses = new ArrayList<>();
        this.events = new ArrayList<>();
    }
    
    public FulfillmentResponse(List<Event> events) {
        this.responses = new ArrayList<>();
        this.events = events;
    }
    
    public FulfillmentResponse(String text) {
        this.responses = Arrays.asList(new Response(text));
        this.events = new ArrayList<>();
    }

    public List<Response> getResponses() {
        return responses;
    }

    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

}
