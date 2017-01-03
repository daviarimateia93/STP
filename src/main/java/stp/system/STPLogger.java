package stp.system;

public abstract class STPLogger {
	
	protected STPLogger() {
		
	}
	
	public static boolean on = true;
	
	private static void print(final Object object) {
		if (on) {
			System.out.print(object);
		}
	}
	
	private static void println(final Object object) {
		print(object);
		print("\r\n");
	}
	
	private static void println() {
		print("\r\n");
	}
	
	private static void printSeparator() {
		println("---------------------------------------------------------------------------------");
	}
	
	public static void welcomeSTP() {
		printSeparator();
		
		println("Welcome to Simple Transfer Protocol - STP");
		println("Created and inspired by: Davi de Sousa Arimateia - daviarimateia93@gmail.com");
		
		printSeparator();
	}
	
	public static void exception(final STPException exception) {
		printSeparator();
		
		println("STPLog: exception occurred");
		println("Code: " + exception.getCode());
		println("Message: " + exception.getMessage());
		
		println(ExceptionHelper.getStackTraceAsString(exception));
		
		printSeparator();
	}
	
	public static void exception(final Exception exception) {
		printSeparator();
		
		println("STPLog: exception occurred");
		println("Message: " + exception.getMessage());
		
		println(ExceptionHelper.getStackTraceAsString(exception));
		
		printSeparator();
	}
	
	public static void byteArray(final byte[] array) {
		printSeparator();
		
		println("STPLog: printing a byte array");
		
		for (final byte b : array) {
			println(b);
		}
		
		println();
		
		printSeparator();
	}
	
	public static void byteArrayAsChar(final byte[] array) {
		printSeparator();
		
		println("STPLog: printing a byte array");
		
		for (final byte b : array) {
			print((char) b);
		}
		
		println();
		
		printSeparator();
	}
	
	public static void info(final Object info) {
		printSeparator();
		
		println("STPLog: INFO!");
		
		println(info);
		
		printSeparator();
	}
	
	public static void error(final Object error) {
		printSeparator();
		
		println("STPLog: ERROR!");
		
		println(error);
		
		printSeparator();
	}
}
