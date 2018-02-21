package stp.core;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class STPExceptionHelper {
	
	protected STPExceptionHelper() {
		
	}
	
	public static String getStackTraceAsString(final Throwable throwable) {
		if (throwable == null) {
			return null;
		}
		
		final StringWriter stringWriter = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(stringWriter);
		
		throwable.printStackTrace(printWriter);
		
		return stringWriter.toString();
	}
}
