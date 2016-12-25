package stp.system;

@SuppressWarnings("serial")
public class STPException extends Exception {
	
	private final int code;
	private final String message;
	
	public STPException(final String message) {
		this(-1, message);
	}
	
	public STPException(final int code, final String message) {
		super(message);
		
		this.code = code;
		this.message = message;
	}
	
	public int getCode() {
		return code;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
