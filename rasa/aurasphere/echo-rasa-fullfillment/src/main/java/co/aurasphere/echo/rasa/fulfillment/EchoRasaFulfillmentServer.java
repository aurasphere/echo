package co.aurasphere.echo.rasa.fulfillment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EchoRasaFulfillmentServer {

    /**
     * Starts the application.
     * 
     * @param args
     *                 null
     */
    public static void main(String[] args) {
        SpringApplication.run(EchoRasaFulfillmentServer.class, args);
    }

}