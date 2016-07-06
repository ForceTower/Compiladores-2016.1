package model;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import exception.InvalidLexemException;
import lexical.LexicalConstants;

public class TokenManager implements LexicalConstants {
	public static List<Token> theTokenLine = new ArrayList<>();;
	private Reader reader;
	private boolean end1 = false;
	public static boolean end = false;
	public boolean isReadingString = false;
	
	public TokenManager() {
		//theTokenLine = new ArrayList<>();
	}
	
	public Token getToken() throws InvalidLexemException, IOException {
		//System.out.println("Is the list empty? " + theTokenLine.isEmpty());
		//System.out.println("Is this the end? " + end1);
		
		if (end1) {
			end = theTokenLine.isEmpty();
		}
		if (theTokenLine.isEmpty() && !end1) {
			Character ch ;
			String lexem = "";
			String prevLexem = null;
			boolean prevIsValid = false;
			
			do {
				ch = getCharacter();
				if(ch != null && Character.isWhitespace(ch) && !isReadingString) {
					//System.out.println("BROKE");
					continue;
				}
				
				if (ch == null){
					//System.out.println("-> The end were reached");
					if (prevIsValid) {
						//System.out.println("  -> Previous state was ok");
						theTokenLine.add(getTokenByLexem(prevLexem));
						prevLexem = null;
						lexem = null;
						//System.out.println("  -> " + prevLexem + " token were created");
						prevIsValid = false;
					} else if (prevLexem != null && !prevLexem.isEmpty()){
						throw new InvalidLexemException(lexem + " is not valid");
						//break;
					}
					
					
					theTokenLine.add(new Token(EOF, "EOF"));
					//System.out.println("-> EOF Token created");
					end1 = true;
					//System.out.println("BROKE!");
					break;
				} else {
					if (ch.equals('"'))
						isReadingString = !isReadingString;
					//System.out.println("-> It's not the end");
					//System.out.println("-> PrevLex: " + prevLexem);
					prevLexem = lexem;
					//System.out.println("-> New PrevLex: " + prevLexem);
					lexem = lexem + ch;
					//System.out.println("-> New Lexem: " + lexem);
					Token t = getTokenByLexem(lexem);
					if (t == null) {
						//System.out.println(lexem + " was rejected by the automata");
						if (prevIsValid) {
							theTokenLine.add(getTokenByLexem(prevLexem));
							//System.out.println("  -> Token with lexem " + prevLexem + " created");
							lexem = ch + "";
							prevLexem = lexem;
							if (getTokenByLexem(prevLexem) != null)
								prevIsValid = true;
							else
								prevIsValid = false;
						} else {
							//return null;
							//throw new InvalidLexemException(lexem + " is not valid");
							continue;
						}
					} else {
						prevIsValid = true;
						prevLexem = lexem;
					}
				}
				
			} while (!Character.isWhitespace(ch) || isReadingString);;
			//System.out.println(" ---------- A WHITE SPACE! --------------");
			if (prevIsValid) {
				//System.out.println("A white space were found, and the prev were valid");
				theTokenLine.add(getTokenByLexem(prevLexem));
				//System.out.println("A token with the lexem " + lexem + " were created");
				prevIsValid = false;
			} else if (prevLexem != null && !lexem.isEmpty()){
				//System.out.println(lexem + " is invalid!");
				throw new InvalidLexemException(lexem + " is invalid!");
			}
		}
		
		try {
			Token t = theTokenLine.remove(0);
			return t;
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
		
	}

	private Token getTokenByLexem(String lexem) throws InvalidLexemException {
		if (lexem == null || lexem.isEmpty())
			throw new InvalidLexemException(lexem + " is not valid");
		
		Token token = null;
		switch (lexem) {
			case "int":
				token = new Token(INT, lexem);
				break;
			case "double":
				token = new Token(DOUBLE, lexem);
				break;
			case "char":
				token = new Token(CHAR, lexem);
				break;
			case "return":
				token = new Token(RETURN, lexem);
				break;
			case "if":
				token = new Token(IF, lexem);
				break;
			case "else":
				token = new Token(ELSE, lexem);
				break;
			case "void":
				token = new Token(VOID, lexem);
				break;
			case "{":
				token = new Token(LKEY, lexem);
				break;
			case "}":
				token = new Token(RKEY, lexem);
				break;
			case "(":
				token = new Token(LPAREN, lexem);
				break;
			case ")":
				token = new Token(RPAREN, lexem);
				break;
			case "[":
				token = new Token(LBRACKET, lexem);
				break;
			case "]":
				token = new Token(RBRACKET, lexem);
				break;
			case "+":
				token = new Token(PLUS, lexem);
				break;
			case "-":
				token = new Token(MINUS, lexem);
				break;
			case "*":
				token = new Token(TIMES, lexem);
				break;
			case "/":
				token = new Token(DIVISION, lexem);
				break;
			case "%":
				token = new Token(PERCENT, lexem);
				break;
			case ";":
				token = new Token(SEMICOLON, lexem);
				break;
			case ",":
				token = new Token(COMMA, lexem);
				break;
			default:
				token = automat(lexem);
		}
		
		return token;
	}

	private Token automat(String lexem) {
		char text[] = lexem.toCharArray();
		char c;
		int state, pos, input;
		pos = 0;
		state = input = 0;
		while (pos < text.length) {
			c = text[pos];
			input = 9;
			if (c == '.')
				input = 1;
			else if (c == '_')
				input = 3;
			else if (c == '&')
				input = 4;
			else if (c == '|')
				input = 5;
			else if (c == '=')
				input = 6;
			else if (c == '!')
				input = 7;
			else if (c == '>')
				input = 10;
			else if (c == '<')
				input = 11;
			else if (Character.isDigit(c))
				input = 0;
			else if (c == '\"')
				input = 8;
			else if (Character.isLetter(c))
				input = 2;
		
			
			state = automata[state][input];
			
			if (state == -1)
				return null;
			pos++;
		}
		Token k = null;
		if (state == 1)
			k = new Token(INTEGER_CONSTANT, lexem);
		else if (state == 2)
			k = new Token(DOUBLE_CONSTANT, lexem);
		else if (state == 3)
			k = new Token(IDENT, lexem);
		else if (state == 4)
			k = new Token(BIT_AND, lexem);
		else if (state == 5)
			k = new Token(AND, lexem);
		else if (state == 6)
			k = new Token(BIT_OR, lexem);
		else if (state == 7)
			k = new Token(OR, lexem);
		else if (state == 8)
			k = new Token(ATTRIB, lexem);
		else if (state == 9)
			k = new Token(EQUALS, lexem);
		else if (state == 10)
			k = new Token(NOT, lexem);
		else if (state == 11)
			k = new Token(NOTEQUALS, lexem);
		else if (state == 13)
			k = new Token(STRING_CONSTANT, lexem);
		else if (state == 14)
			k = new Token(GREATERTHAN, lexem);
		else if (state == 15)
			k = new Token(GREATEREQUALS, lexem);
		else if (state == 16)
			k = new Token(LOWERTHAN, lexem);
		else if (state == 17)
			k = new Token(LOWEREQUALS, lexem);
		
		
		//System.out.println("Automata returned a son: " + k);
		return k;
	}

	public Character getCharacter() throws IOException {
		Character c = null;
		int i = reader.read();

		if (i != -1) {
			c = (char) i;
		}
		return c;
	}
	
	public void unreadCharacter(char c) throws IOException {
		//pbr.unread(c);
	}

	public void setReader(Reader isr) {
		this.reader = isr;
	}

}
