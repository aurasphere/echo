package co.aurasphere.echo.rasa.publisher.model.rasa.nlu;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class NluData {
    
    @SerializedName("lookup_tables")
    private List<LookupTable> lookupTables;
    
    @SerializedName("common_examples")
    private List<CommonExample> commonExamples;
    
    @SerializedName("entity_synonyms")
    private List<EntitySynonym> entitySynonyms;

    public List<LookupTable> getLookupTables() {
        return lookupTables;
    }

    public void setLookupTables(List<LookupTable> lookupTables) {
        this.lookupTables = lookupTables;
    }

    public List<CommonExample> getCommonExamples() {
        return commonExamples;
    }

    public void setCommonExamples(List<CommonExample> commonExamples) {
        this.commonExamples = commonExamples;
    }

    public List<EntitySynonym> getEntitySynonyms() {
        return entitySynonyms;
    }

    public void setEntitySynonyms(List<EntitySynonym> entitySynonyms) {
        this.entitySynonyms = entitySynonyms;
    }

}