package stp.system;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class ExceptionHelper {
	
	protected ExceptionHelper() {
		
	}
	
	public static String getStackTraceAsString(final Throwable throwable) {
		final StringWriter stringWriter = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(stringWriter);
		
		throwable.printStackTrace(printWriter);
		
		return stringWriter.toString();
	}
}
