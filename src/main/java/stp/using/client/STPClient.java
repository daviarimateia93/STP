package stp.using.client;

import java.util.Scanner;

import org.apache.log4j.Logger;

import stp.core.STPException;
import stp.gateway.Peer;
import stp.message.Message;
import stp.message.Payload;
import stp.parser.ParserManager;

public class STPClient {

    private static final Logger logger = Logger.getLogger(STPClient.class);

    public static final String DEFAULT_ADDRESS = "localhost";
    public static final int DEFAULT_PORT = 8888;

    public static void main(final String[] args) {
        String address = DEFAULT_ADDRESS;
        int port = DEFAULT_PORT;

        if (args != null && args.length == 2) {
            address = args[0];
            port = Integer.valueOf(args[1]);
        }

        try {
            final ClientRepeaterParser clientRepeaterParser = new ClientRepeaterParser();
            final ClientCalculatorParser clientCalculatorParser = new ClientCalculatorParser();

            ParserManager.getInstance().add(clientRepeaterParser);
            ParserManager.getInstance().add(clientCalculatorParser);

            final Peer peer = new Peer();
            peer.start(address, port);

            final Scanner scanner = new Scanner(System.in);

            do {
                final String payloadContent = scanner.nextLine();

                final Payload payload = new Payload();
                payload.setContent(payloadContent.getBytes());
                payload.setLength(payloadContent.length());
                payload.setTotalLength(payloadContent.length());
                payload.setPosition(0);

                final Message message = new Message();
                message.setPayload(payload);

                // Request for CalculatorParser
                message.setType(clientCalculatorParser.getType());

                // Request for RepeaterParser
                // message.setType(clientRepeaterParser.getType());

                peer.getTransporter().sendAsync(message);
            } while (scanner.hasNext());

            scanner.close();
        } catch (final STPException exception) {
            logger.error(exception);
        }
    }
}
