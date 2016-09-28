package view_command_line;

import java.io.IOException;

import controller.Compiller;
import exception.IOErrorException;
/**
 * 
 * @author Joao Paulo e Marcelo
 */
public class Main {

	public static void main(String[] args) throws IOException {
		String dir = ".\\Entrada";
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
