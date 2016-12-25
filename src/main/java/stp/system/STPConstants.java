package stp.system;

public class STPConstants {
	
	public static final int EXCEPTION_CODE_GENERAL_EXCEPTION = 0;
	
	public static final int EXCEPTION_CODE_OBJECT_NULL_MESSAGE = 1;
	public static final String EXCEPTION_STR_OBJECT_NULL_MESSAGE = "message object is null";
	
	public static final int EXCEPTION_CODE_OBJECT_NULL_PAYLOAD = 2;
	public static final String EXCEPTION_STR_OBJECT_NULL_PAYLOAD = "payload object is null";
	
	public static final int EXCEPTION_CODE_OBJECT_NULL_PROFILE = 3;
	public static final String EXCEPTION_STR_OBJECT_NULL_PROFILE = "profile object is null";
	
	public static final int EXCEPTION_CODE_PAYLOAD_NULL_CONTENT = 4;
	public static final String EXCEPTION_STR_PAYLOAD_NULL_CONTENT = "payload buffer is null";
	
	public static final int EXCEPTION_CODE_PAYLOAD_OVERLOADED = 5;
	public static final String EXCEPTION_STR_PAYLOAD_OVERLOADED = "payload is overloaded from its capacity";
	
	public static final int EXCEPTION_CODE_PAYLOAD_INVALID_LENGTH = 6;
	public static final String EXCEPTION_STR_PAYLOAD_INVALID_LENGTH = "payload length is invalid, it must be beetween 0 and its total size and minor than PAYLOAD_MAX_SIZE";
	
	public static final int EXCEPTION_CODE_PAYLOAD_INVALID_LENGTH_MATCH = 7;
	public static final String EXCEPTION_STR_PAYLOAD_INVALID_LENGTH_MATCH = "payload length does not match buffer length";
	
	public static final int EXCEPTION_CODE_PAYLOAD_INVALID_POSITION = 8;
	public static final String EXCEPTION_STR_PAYLOAD_INVALID_POSITION = "payload position is invalid";
	
	public static final int EXCEPTION_CODE_MESSAGE_INVALID_ID = 9;
	public static final String EXCEPTION_STR_MESSAGE_INVALID_ID = "message invalid id";
	
	public static final int EXCEPTION_CODE_MESSAGE_NULL_TYPE = 10;
	public static final String EXCEPTION_STR_MESSAGE_NULL_TYPE = "message type is null";
	
	public static final int EXCEPTION_CODE_MESSAGE_EMPTY_TYPE = 11;
	public static final String EXCEPTION_STR_MESSAGE_EMPTY_TYPE = "message type can not be empty";
	
	public static final int EXCEPTION_CODE_MESSAGE_NULL_PAYLOAD = 12;
	public static final String EXCEPTION_STR_MESSAGE_NULL_PAYLOAD = "message payload is null";
	
	public static final int EXCEPTION_CODE_TRANSPORT_INPUTSTREAM_CLOSED = 13;
	public static final String EXCEPTION_STR_TRANSPORT_INPUTSTREAM_CLOSED = "inputstream is closed";
	
	public static final int EXCEPTION_CODE_TRANSPORT_OUTPUTSTREAM_CLOSED = 14;
	public static final String EXCEPTION_STR_TRANSPORT_OUTPUTSTREAM_CLOSED = "outputstream is closed";
	
	public static final int EXCEPTION_CODE_HEADER_INVALID_FORMAT = 15;
	public static final String EXCEPTION_STR_HEADER_INVALID_FORMAT = "invalid header format";
	
	public static final int EXCEPTION_CODE_TRAILER_INVALID_CONTENT = 16;
	public static final String EXCEPTION_STR_TRAILER_INVALID_CONTENT = "invalid trailer content";
	
	public static final int EXCEPTION_CODE_PROFILE_NULL_TYPE = 17;
	public static final String EXCEPTION_STR_PROFILE_NULL_TYPE = "profile type is null";
	
	public static final int EXCEPTION_CODE_PROFILE_EMPTY_TYPE = 18;
	public static final String EXCEPTION_STR_PROFILE_EMPTY_TYPE = "profile type is empty";
	
	public static final int EXCEPTION_CODE_PEER_CONNECTION_CLOSED = 19;
	public static final String EXCEPTION_STR_PEER_CONNECTION_CLOSED = "peer connection closed";
	
	public static final int EXCEPTION_CODE_PARSER_UNKNOWN_TYPE = 20;
	public static final String EXCEPTION_STR_PARSER_UNKNOWN_TYPE = "unknown parser for this type: ";
	
	public static final int EXCEPTION_CODE_INPUTSTREAM_EXCEPTION = 21;
	
	public static final int EXCEPTION_CODE_OUTPUTSTREAM_EXCEPTION = 22;
	
	public static final int EXCEPTION_CODE_SOCKET_EXCEPTION = 23;
	
	public static final int EXCEPTION_CODE_UNKNOWN_HOST_EXCEPTION = 24;
	
	public static final int EXCEPTION_CODE_SECURITY_EXCEPTION = 25;
	
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
