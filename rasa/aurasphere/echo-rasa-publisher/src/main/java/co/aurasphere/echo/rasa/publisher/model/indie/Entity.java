package co.aurasphere.echo.rasa.publisher.model.indie;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class Entity {
    
  private Integer index;
    
    @SerializedName("entity_type")
    private String entityType;

    private String table;
 
    private String column;
    
    private Map<String, List<String>> dictionary;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Map<String, List<String>> getDictionary() {
        return dictionary;
    }

    public void setDictionary(Map<String, List<String>> dictionary) {
        this.dictionary = dictionary;
    }

}