package starters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import exception.LexicalErrorInFile;
import lexical.LexicalAnalyzer;

public class Main {
	
	public static void main (String[] args) {
		LexicalAnalyzer la = new LexicalAnalyzer();
		File kappa = new File("text.x");
		
		try {
			la.analyze(kappa);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LexicalErrorInFile e) {
			System.err.println(e.getMessage());
		}
		
	}

}
