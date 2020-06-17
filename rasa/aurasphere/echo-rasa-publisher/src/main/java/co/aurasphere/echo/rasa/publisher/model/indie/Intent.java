package co.aurasphere.echo.rasa.publisher.model.indie;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Intent {

    private String id;

    private List<Utterance> utterances;

    @SerializedName("followup_utterances")
    private List<Utterance> followupUtterances;

    @SerializedName("response_template")
    private String responseTemplate;

    private List<Parameter> parameters;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Utterance> getFollowupUtterances() {
        return followupUtterances;
    }

    public void setFollowupUtterances(List<Utterance> followupUtterances) {
        this.followupUtterances = followupUtterances;
    }

    public String getResponseTemplate() {
        return responseTemplate;
    }

    public void setResponseTemplate(String responseTemplate) {
        this.responseTemplate = responseTemplate;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public List<Utterance> getUtterances() {
        return utterances;
    }

    public void setUtterances(List<Utterance> utterances) {
        this.utterances = utterances;
    }

}