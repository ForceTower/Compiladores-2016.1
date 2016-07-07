package starters;

import java.io.File;
import java.io.IOException;

import exception.IOErrorException;
import exception.LexicalErrorException;
import lexical.LexicalAnalyzer;

public class Compiler {
	private File file;

	public Compiler() {
		
	}

	public void setFile(String string) throws IOErrorException {
		File f = new File(string);
		if (f.exists())
			file = f;
		else
			throw new IOErrorException("File doesn't exists");
	}
	
	public void lexicalAnalyzer() throws IOErrorException, IOException, LexicalErrorException {
		if (file == null)
			throw new IOErrorException("Set a valid file before starting");
		
		LexicalAnalyzer lexical = new LexicalAnalyzer(file);
		lexical.startAnalysis();
	}
	
	public static void main(String[] args) {
		Compiler comp = new Compiler();
		try {
			comp.setFile("text.x");
			comp.lexicalAnalyzer();
		} catch (IOErrorException | IOException e) {
			e.printStackTrace();
		} catch (LexicalErrorException e) {
			e.printStackTrace();
		}
	}

}
