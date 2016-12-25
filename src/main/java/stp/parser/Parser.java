package stp.parser;

import stp.gateway.Peer;
import stp.message.Message;
import stp.system.STPObject;

public abstract class Parser extends STPObject {
	
	protected ParserManager getParserManager() {
		return ParserManager.getInstance();
	}
	
	protected Message willRead(final Message message) {
		return message;
	}
	
	protected Message willWrite(final Message message) {
		return message;
	}
	
	protected void read(final Peer peer, final Message message) {
		
	}
	
	protected void written(final Peer peer, final Message message) {
		
	}
	
	public abstract String getType();
}
