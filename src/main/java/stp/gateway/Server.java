package stp.gateway;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import stp.parser.ParserManager;
import stp.system.STPConstants;
import stp.system.STPException;
import stp.system.STPLogger;
import stp.system.STPObject;
import stp.system.STPSecurityHelper;

public class Server extends STPObject {
	
	private ServerSocket serverSocket;
	private final ArrayList<Peer> connectedPeers = new ArrayList<>();
	private boolean started;
	
	public Server() {
		
	}
	
	public ArrayList<Peer> getConnectedPeers() {
		return connectedPeers;
	}
	
	public boolean isStarted() {
		return started;
	}
	
	public void start(final int port) throws STPException {
		start(port, null, null);
	}
	
	public void start(final int port, final InputStream keyStoreInputStream, final String keyStorePassword) throws STPException {
		if (!started) {
			try {
				if (keyStoreInputStream != null) {
					final SSLServerSocketFactory sslServerSocketFactory = STPSecurityHelper.getSSLContextForKeyStore(keyStoreInputStream, keyStorePassword).getServerSocketFactory();
					
					final SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);
					
					serverSocket = sslServerSocket;
				} else {
					serverSocket = new ServerSocket(port);
				}
				
				started = true;
				
				STPLogger.info(STPConstants.SERVER_HAS_BEEN_STARTED);
				
				ParserManager.getInstance().onServerStart(this);
				
				onStart();
				
				Socket socket;
				
				while ((socket = serverSocket.accept()) != null) {
					new Peer(socket, this);
				}
			} catch (final IOException exception) {
				throw new STPException(STPConstants.EXCEPTION_CODE_SOCKET_EXCEPTION, exception.getMessage());
			} catch (final STPException exception) {
				STPLogger.exception(exception);
				
				throw exception;
			}
		}
	}
	
	public void end() throws STPException {
		if (started) {
			for (final Peer connectedPeer : connectedPeers) {
				try {
					connectedPeer.end();
				} catch (final STPException stpException) {
					
				}
			}
			
			try {
				serverSocket.close();
			} catch (final IOException exception) {
				throw new STPException(STPConstants.EXCEPTION_CODE_SOCKET_EXCEPTION, exception.getMessage());
			} catch (final Exception exception) {
				throw new STPException(STPConstants.EXCEPTION_CODE_GENERAL_EXCEPTION, exception.getMessage());
			} finally {
				started = false;
				
				STPLogger.info(STPConstants.SERVER_HAS_BEEN_ENDED);
				
				ParserManager.getInstance().onServerEnd(this);
				
				onEnd();
			}
		}
	}
	
	protected void onStart() {
		
	}
	
	protected void onEnd() {
		
	}
	
	protected void onPeerStart(final Peer peer) {
		
	}
	
	protected void onPeerEnd(final Peer peer) {
		
	}
	
	void peerHasStarted(final Peer peer) {
		connectedPeers.add(peer);
		
		ParserManager.getInstance().onPeerStart(this, peer);
		
		onPeerStart(peer);
	}
	
	void peerHasEnded(final Peer peer) {
		connectedPeers.remove(peer);
		
		ParserManager.getInstance().onPeerEnd(this, peer);
		
		onPeerEnd(peer);
	}
}
