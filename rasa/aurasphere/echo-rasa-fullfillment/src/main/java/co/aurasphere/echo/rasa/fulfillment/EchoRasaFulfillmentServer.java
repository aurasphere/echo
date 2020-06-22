package co.aurasphere.echo.rasa.fulfillment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

@SpringBootApplication
public class EchoRasaFulfillmentServer {

    /**
     * Starts the application.
     * 
     * @param args
     *                 null
     */
    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("echo-rasa-fulfillment")
            .build()
            .defaultHelp(true)
            .description("Echo fulfillment endpoint for Rasa.");
        parser.addArgument("-a", "--indie-fulfillment-endpoint")
            .setDefault("https://e127b6288544.ngrok.io/indie/webhook")
            .help("Endpoint of InDie fulfillment server.");
        
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        String indieFulfillmentEndpoint = ns.getString("indie_fulfillment_endpoint");
        SpringApplication.run(EchoRasaFulfillmentServer.class, indieFulfillmentEndpoint);
    }

}