
public class Debug {
	public static boolean isDebug;
	
	public static void print(String message) {
		if (isDebug) {
			System.out.print(message);
		}
	}
	public static void println(String message) {
		print(message + '\n');
	}
}
