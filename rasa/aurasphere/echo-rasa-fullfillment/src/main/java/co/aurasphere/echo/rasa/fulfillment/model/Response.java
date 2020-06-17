package co.aurasphere.echo.rasa.fulfillment.model;

public class Response {
    
    private String text;
    
    public Response(String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}