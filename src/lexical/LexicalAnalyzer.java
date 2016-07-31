package lexical;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import debug.Debug;
import exception.IOErrorException;
import exception.LexicalErrorException;
import model.Token;
import model.TokenFactory;

public class LexicalAnalyzer{
	private File file;
	private File lexResult;
	private BufferedReader reader;
	private BufferedWriter writer;
	public List<Token> allValidTokens;
	public List<Token> allInvalidTokens;
	public int lexicalErrors;
	private Token lastSave = null;

	public LexicalAnalyzer(File arq, File result) throws IOErrorException {
		if (arq != null && arq.canRead() && result != null && result.canWrite()) {
			file = arq;
			lexResult = result;
		} 
		
		else
			throw new IOErrorException("Unreadble file: " + (arq == null ? "NULL FILE" : arq.getName()) + "\nSkipping File...");
		
		createStreams();
		allValidTokens = new ArrayList<>();
		allInvalidTokens = new ArrayList<>();
		lexicalErrors = 0;
	}

	private void createStreams() throws IOErrorException {
		try {
			reader = new BufferedReader(new FileReader(file));
			writer = new BufferedWriter(new FileWriter(lexResult));
		} catch (IOException e) {
			throw new IOErrorException(e.getMessage());
		}
	}

	public void startAnalysis() throws IOException, LexicalErrorException {
		int currentLine = 0;
		String line = null;
		Token hold = null;
		
		while ((line = reader.readLine()) != null) {
			currentLine++;
			Character c = null;
			int position = 0;
			int lineLen = line.length();
			int validToken = -1;
			String str = "";
			
			while (position < lineLen) {
				c = line.charAt(position);
				position++;
				
				if (c == '{') {
					if (!str.isEmpty()) {
						if (validToken == 1)
							validate(hold, hold.getLine(), hold.getPosition());
						else if (validToken == 0)
							invalidate(hold, hold.getLine(), hold.getPosition());
						else {
							Token t = new Token(TokenFactory.LEX_ERROR_INVALID_SYM, str);
							invalidate(t, currentLine, position);
						}
						
						hold = null;
						validToken = -1;
							
					}
					
					Debug.println("Comment started. Line: " + currentLine + ". Position: " + position);
					str = "";
					
					boolean comment = false;
					while (position < lineLen) {
						if (line.charAt(position) == '}') {
							comment = true;
							Debug.println("Comment finished. Line: " + currentLine);
							position++;
							break;
						}
						position++;
					}
					
					if (comment)
						continue;
					/*String subline = line.substring(line.indexOf('{'));
					
					if (subline.contains("}")) {
						position = subline.indexOf('}') + 1;
						line = subline;
						lineLen = subline.length();
						Debug.println("Comment finished. Line: " + currentLine);
						continue;
					} */else {
						while (true) {
							line = reader.readLine();
							
							if (line == null) {
								Debug.println("-> Block Commentary doesn't have an end");
								Token inv_eof = new Token(TokenFactory.LEX_ERROR_COMMENT_END, "~~COMMENTARY~~", currentLine, position);
								invalidate(inv_eof, currentLine, position);
								lineLen = 0;
								break;
							}
	
							currentLine++;
							//writer.newLine();
							
							lastSave = null;
							
							if (!line.contains("}")) {
								Debug.println("Skipping line...");
								continue;
							} else {
								position = line.indexOf("}")+1;
								lineLen = line.length();
								Debug.println("-> Block Comment finished at line: " + currentLine + " at position: " + position);
								break;
							}
						}
						continue;
					}
				}
				
				if (c == ' ' || c == '\t') {
					if (!str.isEmpty()) {
						if (validToken == 1)
							validate(hold, hold.getLine(), hold.getPosition());
						else if (validToken == 0)
							invalidate(hold, hold.getLine(), hold.getPosition());
						else {
							Token t = new Token(TokenFactory.LEX_ERROR_INVALID_SYM, str);
							invalidate(t, currentLine, position);
						}
							
					}
					hold = null;
					validToken = -1;
					str = "";
					continue;
				}
				
				str = str + c;
				
				Token charOnly = TokenFactory.findToken(c.toString());
				
				Token temp = TokenFactory.findToken(str);
				if (temp != null) {
					temp.setLine(currentLine);
					temp.setPosition(position);
					hold = temp;
					validToken = temp.isMalformed() ? 0 : 1;
				} else {
					if (validToken == 1) {
						validate(hold, hold.getLine(), hold.getPosition()); //Valids the previous value
						str = c.toString(); //Gets ready to verify the remaining fragment
						if (charOnly != null) {
							charOnly.setLine(currentLine);
							charOnly.setPosition(position);
							hold = charOnly;
							validToken = charOnly.isMalformed() ? 0 : 1;
						} else {
							hold = charOnly;
							validToken = -1;
						}
					} else if (validToken == 0) {
						invalidate(hold, hold.getLine(), hold.getPosition());
						str = c.toString();
						if (charOnly != null) {
							charOnly.setLine(currentLine);
							charOnly.setPosition(position);
							hold = charOnly;
							validToken = charOnly.isMalformed() ? 0 : 1;
						} else {
							hold = charOnly;
							validToken = -1;
						}
					} else if (validToken == -1) {
						if (charOnly != null) {
							String prevLexeme = str.substring(0, str.length() - 1);
							Token t = new Token(TokenFactory.LEX_ERROR_INVALID_SYM, prevLexeme);
							str = c.toString();
							
							invalidate(t, currentLine, position - 1);
							charOnly.setLine(currentLine);
							charOnly.setPosition(position);
							hold = charOnly;
							validToken = charOnly.isMalformed() ? 0 : 1;
						} else {
							String prevLexeme = str.substring(0, str.length() - 1);
							if (!prevLexeme.trim().isEmpty()) {
								Token t = new Token(TokenFactory.LEX_ERROR_INVALID_SYM, prevLexeme);
								invalidate(t, currentLine, position);
								validToken = -1;
								hold = null;
							}
							str = c.toString();
							//Token t = new Token(TokenFactory.LEX_ERROR_INVALID_SYM, c.toString());
							
							//str = "";
						}
					}
				}
				
				
				if (str.equals("'") || str.equals("\"")) {//We begin a char or string
					hold = null;
					validToken = -1;
					int convType = str.equals("'") ? 0 : 1;
					boolean broke = false;
					while (position < lineLen) {
						c = line.charAt(position);
						str = str + c;
						position++;
						
						if ( (convType == 0 && c == '\'') || (convType == 1 && c == '"') ) {
							broke = true;
							break;
						}
					}
					
					if (!broke) {
						Debug.println("Malformed String or Char: " + str);
						Token t = new Token(convType == 0 ? TokenFactory.LEX_ERROR_MALFORM_CHR : TokenFactory.LEX_ERROR_MALFORM_STR, str);
						
						invalidate(t, currentLine, position);
					} else {
						Token t = TokenFactory.findToken(str);
						if (t == null) {
							t = new Token(convType == 0 ? TokenFactory.LEX_ERROR_MALFORM_CHR : TokenFactory.LEX_ERROR_MALFORM_STR, str); 
							invalidate(t, currentLine, position);
						} else
							validate(t, currentLine, position);
					}
					
					str = "";
				}
			}
			
			if (!str.isEmpty()) {
				if (validToken == 1)
					validate(hold, hold.getLine(), hold.getPosition());
				else if (validToken == 0)
					invalidate(hold, hold.getLine(), hold.getPosition());
				else {
					Token t = new Token(TokenFactory.LEX_ERROR_INVALID_SYM, str);
					invalidate(t, currentLine, position);
				}
				
				hold = null;
				validToken = -1;
					
			}
			
			lastSave = null;
			//writer.newLine();
		}
		
		createResultFile();
		writer.flush();
		writer.close();
	}

	private void createResultFile() throws IOException {
		for (Token t : allValidTokens)
			fileTokenWriter(t);
		
		if (allInvalidTokens.size() == 0) {
			fileStringWriter("Sucesso!");
			Debug.println("Sucesso!");
		} else {
			writer.newLine();
			
			for (Token t : allInvalidTokens)
				fileTokenWriter(t);
		}
	}

	private void validate(Token t, int currentLine, int position) throws IOException {
		/*if (t.getId() == TokenFactory.NUM_CONST && t.getLexem().startsWith("-") && lastSave != null && negativeSpecialCases() ) {
			Token a = TokenFactory.findToken("-");
			Token b = TokenFactory.findToken(t.getLexem().substring(1));
			tokenAuthenticator(a, currentLine, position - t.getLexem().length()-1);
			tokenAuthenticator(b, currentLine, position);
			
			allValidTokens.add(a);
			allValidTokens.add(b);
			Debug.println("Valid Token: " + a);
			Debug.println("Valid Token: " + b);
			lastSave = b;
			
			return;
		}*/
		tokenAuthenticator(t, currentLine, position);
		Debug.println("Valid Token: " + t);
		allValidTokens.add(t);
		lastSave = t;
	}

	private void invalidate(Token t, int currentLine, int position) throws IOException {
		/*t.showDetails(true);
		if (t.getId() == TokenFactory.LEX_ERROR_MALFORM_NUM && t.getLexem().startsWith("-") && lastSave != null && negativeSpecialCases() ) {
			Token a = TokenFactory.findToken("-");
			Token b = TokenFactory.findToken(t.getLexem().substring(1));
			b.errorToken(true);
			a.showDetails(false);
			b.showDetails(true);
			tokenAuthenticator(a, currentLine, position - t.getLexem().length()-1);
			tokenAuthenticator(b, currentLine, position);
			
			allValidTokens.add(a);
			allInvalidTokens.add(b);
			Debug.println("Valid Token: " + a);
			Debug.println("Invalid Token: " + b);
			lastSave = b;
			lexicalErrors++;
			return;
		}*/
		t.errorToken(true);
		tokenAuthenticator(t, currentLine, position);
		Debug.println("Invalid Token: " + t);
		allInvalidTokens.add(t);
		lexicalErrors++;
		lastSave = t;
	}
	
	public boolean negativeSpecialCases() {
		return (lastSave.getId() == TokenFactory.IDENT || lastSave.getId() == TokenFactory.NUM_CONST || lastSave.getId() == TokenFactory.reserved_words.indexOf(")"));
	}

	public void fileStringWriter(String string) throws IOException {
		writer.write(string);
		writer.newLine();
		writer.flush();
	}

	public void tokenAuthenticator(Token t, int currentLine, int position) throws IOException {
		t.setLine(currentLine);
		t.setPosition(position);
		
		//fileTokenWriter(t);
	}

	public void fileTokenWriter(Token token) throws IOException {
		writer.write(token.toString());
		writer.newLine();
		writer.flush();
	}

	public List<Token> getAllTokens() {
		return allValidTokens;
	}

}
