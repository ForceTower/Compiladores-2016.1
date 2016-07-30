package model;

import debug.Debug;

public class TokenFactory implements TokenConstants{
	public static String all_reserved_words_string = "";
	private static boolean init = false;
	
	public static Token findToken(String lexem) {
		if (!init)
			init();
		
		if (lexem.isEmpty())
			return null;
		
		Debug.println("Lexeme: " + lexem);
		
		if (reserved_words.contains(lexem)) {
			Debug.println("Reserved word");
			return new Token(reserved_words.indexOf(lexem), lexem);
		}
		
		Token k = null;
		if (lexem.matches(identifier.pattern()))
			k = new Token(IDENT, lexem);
		else if (lexem.matches(number.pattern()))
			k = new Token(NUM_CONST, lexem);
		else if (lexem.matches(charactere.pattern()))
			k = new Token(CHAR_CONST, lexem);
		else if (lexem.matches(string.pattern()))
			k = new Token(STRING_CONST, lexem);
		else if (lexem.matches(malform_number.pattern()))
			k = new Token(LEX_ERROR_MALFORM_NUM, lexem, true);
		else if (lexem.matches(malform_string.pattern()))
			k = new Token(LEX_ERROR_MALFORM_STR, lexem, true);
		else if(lexem.matches(malform_char.pattern()))
			k = new Token(LEX_ERROR_MALFORM_CHR,lexem,true);
		
		Debug.println("K: " + k);
			
		return k;
	}

	public static void init() {
		addReservedWord("programa", "Reserved Word", 0);
		addReservedWord("const", "Reserved Word", 0);
		addReservedWord("var", "Reserved Word", 0);
		addReservedWord("funcao", "Reserved Word", 0);
		addReservedWord("inicio", "Reserved Word", 0);
		addReservedWord("fim", "Reserved Word", 0);
		addReservedWord("se", "Reserved Word", 0);
		addReservedWord("entao", "Reserved Word", 0);
		addReservedWord("senao", "Reserved Word", 0);
		addReservedWord("enquanto", "Reserved Word", 0);
		addReservedWord("faca", "Reserved Word", 0);
		addReservedWord("leia", "Reserved Word", 0);
		addReservedWord("escreva", "Reserved Word", 0);
		addReservedWord("inteiro", "Reserved Word", 0);
		addReservedWord("real", "Reserved Word", 0);
		addReservedWord("booleano", "Reserved Word", 0);
		addReservedWord("verdadeiro", "Reserved Word", 0);
		addReservedWord("falso", "Reserved Word", 0);
		addReservedWord("cadeia", "Reserved Word", 0);
		addReservedWord("caractere", "Reserved Word", 0);
		
		addReservedWord("+", "Arithmetic Symbol", 1);
		addReservedWord("-", "Arithmetic Symbol", 1);
		addReservedWord("*", "Arithmetic Symbol", 1);
		addReservedWord("/", "Arithmetic Symbol", 1);
		addReservedWord("<>", "Different", 2); //WHATTT?????
		addReservedWord("=", "Attribution", 2);
		addReservedWord("<", "Lower Than", 2);
		addReservedWord("<=", "Lower Equals", 2);
		addReservedWord(">", "Greater Than", 2);
		addReservedWord(">=", "Greater Equals", 2);
		addReservedWord("nao", "Not", 3);
		addReservedWord("e", "And", 3);
		addReservedWord("ou", "Or", 3);
		addReservedWord(";", "Semicolon", 4);
		addReservedWord(",", "Comma", 4);
		addReservedWord("(", "Left Parenthesis", 4);
		addReservedWord(")", "Right Parenthesis", 4);
		
		addMeaning(IDENT, "Identifier");
		addMeaning(NUM_CONST, "Number Constant");
		addMeaning(CHAR_CONST, "Character Constant");
		addMeaning(STRING_CONST, "String Constant");
		
		addMeaning(LEX_ERROR_MALFORM_NUM, "Malformed Number");
		addMeaning(LEX_ERROR_MALFORM_STR, "Malformed String");
		addMeaning(LEX_ERROR_MALFORM_CHR, "Malformed Character");
		addMeaning(LEX_ERROR_INVALID_SYM, "Unknown Symbol");
		addMeaning(LEX_ERROR_COMMENT_END, "Unexpected end of File");
		
		Debug.println("Lexical is Ready");
		init = true;
	}

	private static void addMeaning(int i, String string) {
		meaning_messages.put(i, string);
	}

	private static void addReservedWord(String string, String meaning_message, int type) {
		reserved_words.add(string);
		meaning_messages.put(reserved_words.size()-1, meaning_message);
		
		if (type == 0) {
			if (!all_reserved_words_string.isEmpty())
				all_reserved_words_string = all_reserved_words_string + "|" + string;
			else
				all_reserved_words_string = all_reserved_words_string + string;
		}
	}

}
