package co.aurasphere.echo.rasa.fulfillment.model;

import java.util.Arrays;
import java.util.List;

public class FulfillmentResponse {
    
    private List<Response> responses;
    
    public FulfillmentResponse(String text) {
        this.responses = Arrays.asList(new Response(text));
    }

    public List<Response> getResponses() {
        return responses;
    }

    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }

}
