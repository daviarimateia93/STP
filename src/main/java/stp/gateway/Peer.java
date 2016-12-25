package stp.gateway;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import stp.parser.ParserManager;
import stp.system.STPConstants;
import stp.system.STPException;
import stp.system.STPLogger;
import stp.system.STPObject;
import stp.system.STPSecurityHelper;
import stp.transporter.Transporter;

public class Peer extends STPObject {
	
	private Transporter transporter;
	private Socket socket;
	private Server server;
	private boolean started;
	
	public Peer() {
		
	}
	
	public Peer(final Socket socket, final Server server) {
		this.socket = socket;
		this.server = server;
		
		hasStarted();
	}
	
	public Transporter getTransporter() {
		return transporter;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public Server getServer() {
		return server;
	}
	
	public boolean isStarted() {
		return started;
	}
	
	public void start(final String host, final int port) throws STPException {
		start(host, port, null, null);
	}
	
	public void start(final String host, final int port, final InputStream certificateInputStream, final String certificatePassword) throws STPException {
		if (!started) {
			STPLogger.info(STPConstants.PEER_PREPARING_TO_CONNECT);
			
			try {
				try {
					if (certificateInputStream != null) {
						final SSLSocketFactory sslSocketFactory = STPSecurityHelper.getSSLContextForCertificate(certificateInputStream, certificatePassword).getSocketFactory();
						
						final SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(host, port);
						sslSocket.startHandshake();
						
						socket = sslSocket;
					} else {
						socket = new Socket(host, port);
					}
					
					hasStarted();
				} catch (final UnknownHostException exception) {
					throw new STPException(STPConstants.EXCEPTION_CODE_UNKNOWN_HOST_EXCEPTION, exception.getMessage());
				} catch (final IOException exception) {
					throw new STPException(STPConstants.EXCEPTION_CODE_SOCKET_EXCEPTION, exception.getMessage());
				}
			} catch (final STPException exception) {
				STPLogger.exception(exception);
				
				end();
				
				throw exception;
			}
		}
	}
	
	public void end() throws STPException {
		if (started) {
			try {
				socket.close();
			} catch (final IOException exception) {
				throw new STPException(STPConstants.EXCEPTION_CODE_SOCKET_EXCEPTION, exception.getMessage());
			} catch (final Exception exception) {
				throw new STPException(STPConstants.EXCEPTION_CODE_GENERAL_EXCEPTION, exception.getMessage());
			} finally {
				hasEnded();
			}
		}
	}
	
	protected void onStart() {
		
	}
	
	protected void onEnd() {
		
	}
	
	private void hasStarted() {
		transporter = new Transporter(this, socket);
		transporter.start();
		
		started = true;
		
		STPLogger.info(STPConstants.PEER_CONNECTED_SUCCESSFULLY);
		
		ParserManager.getInstance().onPeerStart(this);
		
		onStart();
		
		if (server != null) {
			server.peerHasStarted(this);
		}
	}
	
	private void hasEnded() {
		started = false;
		
		STPLogger.info(STPConstants.PEER_CONNECTION_ENDED);
		
		ParserManager.getInstance().onPeerEnd(this);
		
		onEnd();
		
		if (server != null) {
			server.peerHasEnded(this);
		}
	}
}
