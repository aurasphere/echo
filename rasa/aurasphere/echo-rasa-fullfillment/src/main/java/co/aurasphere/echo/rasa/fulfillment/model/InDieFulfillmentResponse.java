package co.aurasphere.echo.rasa.fulfillment.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InDieFulfillmentResponse {
    
    private String error;
    
    @JsonProperty("fulfillment_message")
    private String fulfillmentMessage;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getFulfillmentMessage() {
        return fulfillmentMessage;
    }

    public void setFulfillmentMessage(String fulfillmentMessage) {
        this.fulfillmentMessage = fulfillmentMessage;
    }

}
