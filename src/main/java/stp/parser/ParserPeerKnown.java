package stp.parser;

import stp.gateway.Peer;

public interface ParserPeerKnown {
	
	public void onPeerStart(final Peer peer);
	
	public void onPeerEnd(final Peer peer);
}
