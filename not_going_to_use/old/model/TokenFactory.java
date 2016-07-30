package model;

import debug.Debug;

public class TokenFactory implements TokenConstants{
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

	private static void init() {
		addReservedWord("programa", "Reserved Word");
		addReservedWord("const", "Reserved Word");
		addReservedWord("var", "Reserved Word");
		addReservedWord("funcao", "Reserved Word");
		addReservedWord("inicio", "Reserved Word");
		addReservedWord("fim", "Reserved Word");
		addReservedWord("se", "Reserved Word");
		addReservedWord("entao", "Reserved Word");
		addReservedWord("senao", "Reserved Word");
		addReservedWord("enquanto", "Reserved Word");
		addReservedWord("faca", "Reserved Word");
		addReservedWord("leia", "Reserved Word");
		addReservedWord("escreva", "Reserved Word");
		addReservedWord("inteiro", "Reserved Word");
		addReservedWord("real", "Reserved Word");
		addReservedWord("booleano", "Reserved Word");
		addReservedWord("verdadeiro", "Reserved Word");
		addReservedWord("falso", "Reserved Word");
		addReservedWord("cadeia", "Reserved Word");
		addReservedWord("caractere", "Reserved Word");
		
		addReservedWord("+", "Arithmetic Symbol");
		addReservedWord("-", "Arithmetic Symbol");
		addReservedWord("*", "Arithmetic Symbol");
		addReservedWord("/", "Arithmetic Symbol");
		addReservedWord("<>", "Different"); //WHATTT?????
		addReservedWord("=", "Attribution");
		addReservedWord("<", "Lower Than");
		addReservedWord("<=", "Lower Equals");
		addReservedWord(">", "Greater Than");
		addReservedWord(">=", "Greater Equals");
		addReservedWord("nao", "Not");
		addReservedWord("e", "And");
		addReservedWord("ou", "Or");
		addReservedWord(";", "Semicolon");
		addReservedWord(",", "Comma");
		addReservedWord("(", "Left Parenthesis");
		addReservedWord(")", "Right Parenthesis");
		
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

	private static void addReservedWord(String string, String meaning_message) {
		reserved_words.add(string);
		meaning_messages.put(reserved_words.size()-1, meaning_message);
	}

}
