package stp.using.server;

import stp.core.STPException;
import stp.gateway.Peer;
import stp.message.Message;
import stp.message.Payload;
import stp.parser.Parser;

public class ServerCalculatorParser extends Parser {

    private static final String TYPE = "CALCULATOR";

    public static final String OPERATOR_PLUS = "+";
    public static final String OPERATOR_MINUS = "-";
    public static final String OPERATOR_TIMES = "*";
    public static final String OPERATOR_DIVIDE = "/";

    @Override
    public void read(final Peer peer, final Message message) {
        System.out.println("READ");
        System.out.println(message.toString());

        try {
            final String expression = new String(message.getPayload().getContent());

            String response = "";

            if (!expression.matches("[\\+\\-\\*\\/]\\;[0-9]+(\\.[0-9]+)?\\;[0-9]+(\\.[0-9]+)?")) {
                response = "Invalid format: [\\+\\-\\*\\/]\\;[0-9]+(\\.[0-9]+)?\\;[0-9]+(\\.[0-9]+)?";
            } else {
                final String[] expressionSplitted = expression.split(";");

                final String operator = expressionSplitted[0];

                final Double number1 = Double.valueOf(expressionSplitted[1]);
                final Double number2 = Double.valueOf(expressionSplitted[2]);

                switch (operator) {
                case OPERATOR_PLUS: {
                    response = String.valueOf(number1 + number2);

                    break;
                }

                case OPERATOR_MINUS: {
                    response = String.valueOf(number1 - number2);

                    break;
                }

                case OPERATOR_TIMES: {
                    response = String.valueOf(number1 * number2);

                    break;
                }

                case OPERATOR_DIVIDE: {
                    if (number2 != 0) {
                        response = String.valueOf(number1 / number2);
                    } else {
                        response = "Denominator musn't be 0 (zero)";
                    }

                    break;
                }
                }
            }

            final Payload payload = new Payload();
            payload.setContent(response.getBytes());
            payload.setLength(response.length());
            payload.setTotalLength(response.length());
            payload.setPosition(0);

            message.setPayload(payload);

            peer.getTransporter().sendAsync(message);
        } catch (final STPException exception) {
            System.out.println("EXCEPTION");
            System.out.println(exception.getCode() + ": " + exception.getMessage());
        }
    }

    @Override
    protected void written(final Peer peer, final Message message) {
        System.out.println("WRITTEN");
        System.out.println(message.toString());
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
