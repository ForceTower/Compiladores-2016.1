package starters;

import java.io.IOException;

import controller.Compiller;
import exception.IOErrorException;

public class Main {

	public static void main(String[] args) throws IOException {
		String dir = ".\\file_tests\\";
		if (args.length == 1)
			dir = args[0];
		
		try {
			Compiller c = new Compiller();
			c.setDirectory(dir);
			c.analysisAll();
		} catch (IOErrorException e) {
			e.printStackTrace();
		}
	}
}
