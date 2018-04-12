package stp.gateway;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import org.apache.log4j.Logger;

import stp.core.STPException;
import stp.core.STPObject;
import stp.core.STPSecurityHelper;
import stp.parser.ParserManager;

public class Server extends STPObject {

    private static final Logger logger = Logger.getLogger(Server.class);

    public static final String SERVER_HAS_BEEN_STARTED = "Server - has been started";
    public static final String SERVER_HAS_BEEN_ENDED = "Server - has been ended";

    private ServerSocket serverSocket;
    private final ArrayList<Peer> connectedPeers = new ArrayList<>();
    private boolean started;

    public List<Peer> getConnectedPeers() {
        return connectedPeers;
    }

    public boolean isStarted() {
        return started;
    }

    public void start(final int port) throws STPException {
        start(port, null, null);
    }

    public void start(final int port, final InputStream keyStoreInputStream, final String keyStorePassword)
            throws STPException {
        if (!started) {
            try {
                if (keyStoreInputStream != null) {
                    final SSLServerSocketFactory sslServerSocketFactory = STPSecurityHelper
                            .getSSLContextForKeyStore(keyStoreInputStream, keyStorePassword).getServerSocketFactory();

                    final SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory
                            .createServerSocket(port);

                    serverSocket = sslServerSocket;
                } else {
                    serverSocket = new ServerSocket(port);
                }

                started = true;

                logger.info(SERVER_HAS_BEEN_STARTED);

                ParserManager.getInstance().onServerStart(this);

                onStart();

                Socket socket;

                while ((socket = serverSocket.accept()) != null) {
                    new Peer(socket, this);
                }
            } catch (final IOException exception) {
                logger.error(exception);

                throw new STPException(STPException.EXCEPTION_CODE_SOCKET_EXCEPTION, exception);
            } catch (final STPException exception) {
                logger.error(exception);

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
                throw new STPException(STPException.EXCEPTION_CODE_SOCKET_EXCEPTION, exception);
            } catch (final Exception exception) {
                throw new STPException(exception);
            } finally {
                started = false;

                logger.info(SERVER_HAS_BEEN_ENDED);

                ParserManager.getInstance().onServerEnd(this);

                onEnd();
            }
        }
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

    protected void onStart() {
        // Can be overridden
    }

    protected void onEnd() {
        // Can be overridden
    }

    protected void onPeerStart(final Peer peer) {
        // Can be overridden
    }

    protected void onPeerEnd(final Peer peer) {
        // Can be overridden
    }
}
