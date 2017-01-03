package stp.system;

public class STPException extends Exception {
	
	private static final long serialVersionUID = -602084367629893802L;
	
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
	
	public STPException(final Throwable throwable) {
		super(throwable);
		
		this.code = STPConstants.EXCEPTION_CODE_GENERAL_EXCEPTION;
		this.message = ExceptionHelper.getStackTraceAsString(throwable);
	}
	
	public int getCode() {
		return code;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
