package co.aurasphere.echo.rasa.fulfillment.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InDieFulfillmentRequest {
    
    @JsonProperty("intent_id")
    private String intentId;
    
    @JsonProperty("slot_values")
    private Map<?,?> slotValues;
    

    @JsonProperty("response_template")
    private String responseTemplate;

    public InDieFulfillmentRequest(String intentId, Map<?,?> slotValues, String responseTemplate) {
        this.intentId = intentId;
        this.slotValues = slotValues;
        this.responseTemplate = responseTemplate;
    }
    
    public String getIntentId() {
        return intentId;
    }

    public void setIntentId(String intentId) {
        this.intentId = intentId;
    }

    public Map<?,?> getSlotValues() {
        return slotValues;
    }

    public void setSlotValues(Map<?,?> slotValues) {
        this.slotValues = slotValues;
    }

    public String getResponseTemplate() {
        return responseTemplate;
    }

    public void setResponseTemplate(String responseTemplate) {
        this.responseTemplate = responseTemplate;
    }

}
