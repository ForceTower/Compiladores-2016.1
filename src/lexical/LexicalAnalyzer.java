package lexical;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;

import exception.InvalidLexemException;
import exception.LexicalErrorInFile;
import model.Token;
import model.TokenManager;

public class LexicalAnalyzer{
	public static int lexicalErrors = 0;
	private TokenManager tm;
	
	public LexicalAnalyzer() {
		tm = new TokenManager();
	}
	
	public void init() {
		
	}

	public void analyze(File file) throws IOException, LexicalErrorInFile {
		lexicalErrors = 0;
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis);
		PushbackReader pbr = new PushbackReader(isr);
		//FileReader fr = new FileReader(file);
		tm.setReader(pbr);
		//fr.re
		
		Token t = null;
		do {
			try {
				t = tm.getToken();
				if (t != null) {
					t.confirm();
					System.out.println(t);
				}
			} catch (InvalidLexemException e) {
				System.err.println("\n" + e.getMessage() + "\n");
				lexicalErrors++;
			}
		} while (!TokenManager.end);
		
		//System.out.println(lexicalErrors + " - Lexical Errors Found");
		if (lexicalErrors > 0) {
			if (lexicalErrors == 1)
				throw new LexicalErrorInFile("1 Lexical error was found");
			else
				throw new LexicalErrorInFile(lexicalErrors + " errors were found");
		}
		//System.out.println("All Tokens:\n" + Token.instances);
	}
	

}
