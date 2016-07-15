package starters;

import java.io.IOException;

import controller.Compiller;
import exception.IOErrorException;

public class Main {

	public static void main(String[] args) throws IOException {
		try {
			Compiller c = new Compiller();
			c.setDirectory(".\\files\\");
			c.analysisAll();
		} catch (IOErrorException e) {
			e.printStackTrace();
		}
	}
}
