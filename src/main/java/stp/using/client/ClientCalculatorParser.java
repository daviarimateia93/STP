package stp.using.client;

import stp.gateway.Peer;
import stp.message.Message;
import stp.message.Payload;
import stp.parser.Parser;

public final class ClientCalculatorParser extends Parser {
	
	private static final String TYPE = "CALCULATOR";
	
	public static final String OPERATOR_PLUS = "+";
	public static final String OPERATOR_MINUS = "-";
	public static final String OPERATOR_TIMES = "*";
	public static final String OPERATOR_DIVIDE = "/";
	
	private Payload generatePayload(final String expression, final String operator) {
		final String[] numbers = expression.split("\\" + operator);
		final String content = operator + ";" + numbers[0] + ";" + numbers[1];
		
		final Payload payload = new Payload();
		payload.setContent(content.getBytes());
		payload.setLength(content.length());
		payload.setTotalLength(content.length());
		payload.setPosition(0);
		
		return payload;
	}
	
	private Double[] getNumbers(String expression, String operator) {
		final String[] numbers = expression.split("\\" + operator);
		
		return new Double[] { Double.valueOf(numbers[0]), Double.valueOf(numbers[1]) };
	}
	
	@Override
	protected Message willWrite(final Message message) {
		final String expression = new String(message.getPayload().getContent());
		
		Payload payload = null;
		
		if (!expression.matches("[0-9]+(\\.[0-9]+)?[\\+\\-\\*\\/][0-9]+(\\.[0-9]+)?")) {
			System.out.println("Invalid format: [0-9]+(\\.[0-9]+)?[\\+\\-\\*\\/][0-9]+(\\.[0-9]+)?");
		} else {
			if (expression.indexOf(OPERATOR_PLUS) > -1) {
				payload = generatePayload(expression, OPERATOR_PLUS);
			} else if (expression.indexOf(OPERATOR_MINUS) > -1) {
				payload = generatePayload(expression, OPERATOR_MINUS);
			} else if (expression.indexOf(OPERATOR_TIMES) > -1) {
				payload = generatePayload(expression, OPERATOR_TIMES);
			} else if (expression.indexOf(OPERATOR_DIVIDE) > -1) {
				final Double[] numbers = getNumbers(expression, OPERATOR_DIVIDE);
				
				if (numbers[1] != 0) {
					payload = generatePayload(expression, OPERATOR_DIVIDE);
				} else {
					System.out.println("Denominator musn't be 0 (zero)");
				}
			}
		}
		
		if (payload != null) {
			message.setPayload(payload);
			
			return message;
		} else {
			// Returning null makes transport ignore the message
			return null;
		}
	};
	
	@Override
	protected void read(final Peer peer, final Message message) {
		System.out.println("READ");
		System.out.println(message.toString());
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
