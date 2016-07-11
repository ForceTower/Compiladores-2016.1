package model;

import debug.Debug;
import lexical.LexicalConstants;

public class TokenFactory implements LexicalConstants{

	public static Token findToken(String lexem) {
		if (lexem == null) {
			Debug.println("Null lexem");
			return null;
		} else if (lexem.isEmpty()) {
			Debug.println("Empty lexem");
			return null;
		} 
		
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
			case ">=":
				token = new Token(GREATEREQUALS, lexem);
				break;
			case "<=":
				token = new Token(LOWEREQUALS, lexem);
				break;
			default:
				token = automata(lexem);
		}
		
		return token;
	}
	
	private static Token automata(String lexem) {
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
			else if (c == '-')
				input = 12;
			else if (c == '\'')
				input = 13;
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
		else if (state == 21)
			k = new Token(CHAR_CONSTANT,lexem);
		
		return k;
	}

	public static Token createToken(int id, String lexem) {
		return new Token(id, lexem);
	}

}
