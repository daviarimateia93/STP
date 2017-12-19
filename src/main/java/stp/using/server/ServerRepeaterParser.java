package stp.using.server;

import stp.core.STPException;
import stp.gateway.Peer;
import stp.message.Message;
import stp.parser.Parser;

public class ServerRepeaterParser extends Parser {
	
	private static final String TYPE = "REPEATER";
	
	@Override
	protected void read(final Peer peer, final Message message) {
		try {
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
