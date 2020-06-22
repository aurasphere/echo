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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((entityType == null) ? 0 : entityType.hashCode());
        result = prime * result + ((friendlyName == null) ? 0 : friendlyName.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((mandatory == null) ? 0 : mandatory.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Parameter other = (Parameter) obj;
        if (entityType == null) {
            if (other.entityType != null)
                return false;
        } else if (!entityType.equals(other.entityType))
            return false;
        if (friendlyName == null) {
            if (other.friendlyName != null)
                return false;
        } else if (!friendlyName.equals(other.friendlyName))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (mandatory == null) {
            if (other.mandatory != null)
                return false;
        } else if (!mandatory.equals(other.mandatory))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Parameter [id=" + id + ", mandatory=" + mandatory + ", entityType=" + entityType + ", friendlyName=" + friendlyName + "]";
    }

}
