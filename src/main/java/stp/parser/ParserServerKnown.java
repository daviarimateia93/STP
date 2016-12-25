package stp.parser;

import stp.gateway.Peer;
import stp.gateway.Server;

public interface ParserServerKnown {
	
	public void onServerStart(final Server server);
	
	public void onServerEnd(final Server server);
	
	public void onPeerStart(final Server server, final Peer peer);
	
	public void onPeerEnd(final Server server, final Peer peer);
}
