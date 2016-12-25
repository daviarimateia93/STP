package stp.using.client;

import stp.gateway.Peer;
import stp.message.Message;
import stp.parser.Parser;

public class ClientRepeaterParser extends Parser {
	
	private static final String TYPE = "REPEATER";
	
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
