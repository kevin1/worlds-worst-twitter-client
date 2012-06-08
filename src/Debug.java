
public class Debug {
	// Is this application running in debug mode?
	public static boolean isDebug;
	
	/**
	 * Print a debug message to the command line / console, if isDebug is true. 
	 * @param message The message to be printed.
	 */
	public static void print(String message) {
		if (isDebug) {
			System.out.print(message);
		}
	}
	
	/**
	 * Print a debug message to the command line / console and append a newline character, if isDebug is true.
	 * @param message
	 */
	public static void println(String message) {
		print(message + '\n');
	}
}
