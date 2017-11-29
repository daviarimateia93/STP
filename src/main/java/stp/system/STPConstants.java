package stp.system;

public abstract class STPConstants {
	
	protected STPConstants() {
		
	}
	
	public static final int PAYLOAD_MAX_SIZE = 4096;
	
	public static final int MSG_MAX_SIZE = 512 + PAYLOAD_MAX_SIZE;
	
	public static final int MSG_ID_LENGTH = 36;
	
	public static final String MSG_TAG = "MSG";
	
	public static final int STP_HEADER_PIECES_LENGTH = 6;
	
	public static final int STP_HEADER_INDEX_TAG = 0;
	public static final int STP_HEADER_INDEX_TYPE = 1;
	public static final int STP_HEADER_INDEX_ID = 2;
	public static final int STP_HEADER_INDEX_PAYLOAD_POSITION = 3;
	public static final int STP_HEADER_INDEX_PAYLOAD_CONTENT_LENGTH = 4;
	public static final int STP_HEADER_INDEX_PAYLOAD_TOTAL_LENGTH = 5;
	
	public static final byte[] STP_TRAILER = { 'E', 'N', 'D', '\r', '\n' };
	
	public static final String TRANSPORT_PREPARING_TO_SEND_MESSAGE = "Transport - preparing to send message";
	public static final String TRANSPORT_MESSAGE_SENT = "Transport - sent message";
	
	public static final String TRANSPORT_PREPARING_TO_READ_MESSAGE = "Transport - preparing to read message";
	public static final String TRANSPORT_MESSAGE_READ = "Transport - message read";
	
	public static final String TRANSPORT_MESSAGE_IGNORED = "Transport - message ignored";
	
	public static final String SERVER_HAS_BEEN_STARTED = "Server - has been started";
	public static final String SERVER_HAS_BEEN_ENDED = "Server - has been ended";
	
	public static final String PEER_PREPARING_TO_CONNECT = "Peer - preparing to connect";
	public static final String PEER_CONNECTED_SUCCESSFULLY = "Peer - connected successfully";
	public static final String PEER_CONNECTION_ENDED = "Peer - connection ended";
}
