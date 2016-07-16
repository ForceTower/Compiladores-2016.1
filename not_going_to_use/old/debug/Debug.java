package debug;

public class Debug {
	public static boolean DEBUG = false;
	
	public static void println(String str) {
		if (DEBUG)
			System.out.println(str);
	}

}
