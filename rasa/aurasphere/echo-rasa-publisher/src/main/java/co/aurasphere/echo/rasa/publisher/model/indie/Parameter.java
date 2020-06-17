package co.aurasphere.echo.rasa.publisher.model.indie;

import com.google.gson.annotations.SerializedName;

public class Parameter {
    
    private String id;
    
    private Boolean mandatory;
    
    @SerializedName("entity_type")
    private String entityType;
    
    @SerializedName("friendly_name")
    private String friendlyName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

}
