package co.aurasphere.echo.rasa.fulfillment;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import co.aurasphere.echo.rasa.fulfillment.model.FulfillmentResponse;
import co.aurasphere.echo.rasa.fulfillment.model.InDieFulfillmentRequest;
import co.aurasphere.echo.rasa.fulfillment.model.InDieFulfillmentResponse;

@RestController
public class FulfillmentController {
    
    private RestTemplate restTemplate = new RestTemplate();
    
    private final static String INDIE_ENDPOINT = "https://e127b6288544.ngrok.io/indie/webhook";

    @PostMapping("/webhook")
    public FulfillmentResponse webhook(@RequestBody String request) {
        System.out.println(request);
        JSONObject root = new JSONObject(request);
        String intentId = root.getString("next_action").replace("action_send_server_", "");
        Map<?, ?> slotValues = root.getJSONObject("tracker").getJSONObject("slots").toMap();
        String responseTemplate = root.getJSONObject("domain").getJSONObject("responses").getJSONArray("utter_" + intentId).getJSONObject(0).getString("text");
        
        System.out.println(intentId);
        System.out.println(slotValues);
        System.out.println(responseTemplate);
        InDieFulfillmentRequest indieRequest = new InDieFulfillmentRequest(intentId, slotValues, responseTemplate);
        
        InDieFulfillmentResponse response = restTemplate.postForObject(INDIE_ENDPOINT, indieRequest, InDieFulfillmentResponse.class);
        
        String textResponse = response.getFulfillmentMessage();
        if (textResponse== null) {
            textResponse = response.getError();
        }
        return new FulfillmentResponse(textResponse);
    }
    
}