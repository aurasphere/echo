package co.aurasphere.echo.rasa.fulfillment;

import java.util.Arrays;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import co.aurasphere.echo.rasa.fulfillment.model.Event;
import co.aurasphere.echo.rasa.fulfillment.model.FulfillmentResponse;
import co.aurasphere.echo.rasa.fulfillment.model.InDieFulfillmentRequest;
import co.aurasphere.echo.rasa.fulfillment.model.InDieFulfillmentResponse;

@RestController
public class FulfillmentController {

    private RestTemplate restTemplate = new RestTemplate();

    private String indieEndpoint;

    @Autowired
    public FulfillmentController(ApplicationArguments arguments) {
        this.indieEndpoint = arguments.getSourceArgs()[0];
    }

    @PostMapping("/webhook")
    public FulfillmentResponse webhook(@RequestBody String request) {
        System.out.println(request);
        JSONObject root = new JSONObject(request);
        String action = root.getString("next_action")
            .replace("action_", "");
        FulfillmentResponse response = null;
        if (action.startsWith("send_server")) {
            response = handleSendServerAction(action.replace("send_server_", ""), root);
        }
        if (action.startsWith("map_slots")) {
            response = handleMapSlotsAction(action.replace("map_slots_", ""), root);
        }
        return response;
    }

    private FulfillmentResponse handleMapSlotsAction(String slotsMapping, JSONObject request) {
        String[] slots = slotsMapping.split("_");
        String fromSlot = slots[0];
        String toSlot = slots[1];

        Map<?, ?> slotValues = request.getJSONObject("tracker")
            .getJSONObject("slots")
            .toMap();
        String slotValue = slotValues.get(fromSlot)
            .toString();

        Event event = new Event("slot", toSlot, slotValue);
        return new FulfillmentResponse(Arrays.asList(event));
    }

    private FulfillmentResponse handleSendServerAction(String intentId, JSONObject request) {
        Map<?, ?> slotValues = request.getJSONObject("tracker")
            .getJSONObject("slots")
            .toMap();
        String responseTemplate = request.getJSONObject("domain")
            .getJSONObject("responses")
            .getJSONArray("utter_" + intentId)
            .getJSONObject(0)
            .getString("text");

        System.out.println("Detected intent: " +intentId);
        System.out.println("Slots values: " + slotValues);
        System.out.println("Response template: " + responseTemplate);
        InDieFulfillmentRequest indieRequest = new InDieFulfillmentRequest(intentId, slotValues, responseTemplate);

        InDieFulfillmentResponse response = restTemplate.postForObject(indieEndpoint, indieRequest, InDieFulfillmentResponse.class);
        System.out.println("Response from indie server: " + response);
        
        String textResponse = response.getFulfillmentMessage();
        if (textResponse == null) {
            textResponse = response.getError();
        }
        return new FulfillmentResponse(textResponse);
    }

}