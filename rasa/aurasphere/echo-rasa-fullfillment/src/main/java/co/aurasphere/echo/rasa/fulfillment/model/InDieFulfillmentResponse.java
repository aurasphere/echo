package co.aurasphere.echo.rasa.fulfillment.model;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InDieFulfillmentResponse {
    
    private String error;
    
    @JsonProperty("fulfillment_message")
    private String fulfillmentMessage;
    
    private String[][] table;

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

    public String[][] getTable() {
        return table;
    }

    public void setTable(String[][] table) {
        this.table = table;
    }

    @Override
    public String toString() {
        return "InDieFulfillmentResponse [error=" + error + ", fulfillmentMessage=" + fulfillmentMessage + ", table=" + Arrays.toString(table) + "]";
    }


}
