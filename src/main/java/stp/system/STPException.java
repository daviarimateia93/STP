package stp.system;

public class STPException extends Exception {
	
	private static final long serialVersionUID = -602084367629893802L;
	
	private static final int CODE_GENERAL_EXCEPTION = 0;
	
	public static final int EXCEPTION_CODE_OBJECT_NULL_MESSAGE = 1;
	public static final int EXCEPTION_CODE_OBJECT_NULL_PAYLOAD = 2;
	public static final int EXCEPTION_CODE_OBJECT_NULL_PROFILE = 3;
	public static final int EXCEPTION_CODE_PAYLOAD_NULL_CONTENT = 4;
	public static final int EXCEPTION_CODE_PAYLOAD_OVERLOADED = 5;
	public static final int EXCEPTION_CODE_PAYLOAD_INVALID_LENGTH = 6;
	public static final int EXCEPTION_CODE_PAYLOAD_INVALID_LENGTH_MATCH = 7;
	public static final int EXCEPTION_CODE_PAYLOAD_INVALID_POSITION = 8;
	public static final int EXCEPTION_CODE_MESSAGE_INVALID_ID = 9;
	public static final int EXCEPTION_CODE_MESSAGE_NULL_TYPE = 10;
	public static final int EXCEPTION_CODE_MESSAGE_EMPTY_TYPE = 11;
	public static final int EXCEPTION_CODE_MESSAGE_NULL_PAYLOAD = 12;
	public static final int EXCEPTION_CODE_TRANSPORT_INPUTSTREAM_CLOSED = 13;
	public static final int EXCEPTION_CODE_TRANSPORT_OUTPUTSTREAM_CLOSED = 14;
	public static final int EXCEPTION_CODE_HEADER_INVALID_FORMAT = 15;
	public static final int EXCEPTION_CODE_TRAILER_INVALID_CONTENT = 16;
	public static final int EXCEPTION_CODE_PROFILE_NULL_TYPE = 17;
	public static final int EXCEPTION_CODE_PROFILE_EMPTY_TYPE = 18;
	public static final int EXCEPTION_CODE_PEER_CONNECTION_CLOSED = 19;
	public static final int EXCEPTION_CODE_PARSER_UNKNOWN_TYPE = 20;
	public static final int EXCEPTION_CODE_INPUTSTREAM_EXCEPTION = 21;
	public static final int EXCEPTION_CODE_OUTPUTSTREAM_EXCEPTION = 22;
	public static final int EXCEPTION_CODE_SOCKET_EXCEPTION = 23;
	public static final int EXCEPTION_CODE_UNKNOWN_HOST_EXCEPTION = 24;
	public static final int EXCEPTION_CODE_SECURITY_EXCEPTION = 25;
	
	private final int code;
	private final String message;
	
	public STPException(final String message) {
		this(CODE_GENERAL_EXCEPTION, message);
	}
	
	public STPException(final int code) {
		this(code, (Throwable) null);
	}
	
	public STPException(final int code, final String message) {
		super(message);
		
		this.code = code;
		this.message = message;
	}
	
	public STPException(final Throwable throwable) {
		this(CODE_GENERAL_EXCEPTION, throwable);
	}
	
	public STPException(final int code, final Throwable throwable) {
		super(throwable);
		
		this.code = code;
		this.message = STPExceptionHelper.getStackTraceAsString(throwable);
	}
	
	public int getCode() {
		return code;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
