package model;

import debug.Debug;

public class TokenFactory implements TokenConstants{
	private static boolean init = false;
	
	public static Token findToken(String lexem) {
		if (!init)
			init();
		
		if (lexem.isEmpty())
			return null;
		
		if (reserved_words.contains(lexem))
			return new Token(reserved_words.indexOf(lexem), lexem);
		
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
		else if (lexem.matches(malform_number_2.pattern()))
			k = new Token(LEX_ERROR_MALFORM_NUM, lexem, true);
		else if (lexem.matches(malform_string.pattern()))
			k = new Token(LEX_ERROR_MALFORM_STR, lexem, true);
		else if(lexem.matches(malform_char.pattern()))
			k = new Token(LEX_ERROR_MALFORM_CHR,lexem,true);
			
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
		
		addReservedWord("+", "Operator/Arithmetic Symbol", 1);
		addReservedWord("-", "Operator/Arithmetic Symbol", 1);
		addReservedWord("*", "Operator/Arithmetic Symbol", 1);
		addReservedWord("/", "Operator/Arithmetic Symbol", 1);
		addReservedWord("<>", "Operator/Different", 2);
		addReservedWord("=", "Operator/Attribution", 2);
		addReservedWord("<", "Operator/Lower Than", 2);
		addReservedWord("<=", "Operator/Lower Equals", 2);
		addReservedWord(">", "Operator/Greater Than", 2);
		addReservedWord(">=", "Operator/Greater Equals", 2);
		addReservedWord("nao", "Operator/Not", 3);
		addReservedWord("e", "Operator/And", 3);
		addReservedWord("ou", "Operator/Or", 3);
		addReservedWord(";", "Delimiter/Semicolon", 4);
		addReservedWord(",", "Delimiter/Comma", 4);
		addReservedWord("(", "Delimiter/Left Parenthesis", 4);
		addReservedWord(")", "Delimiter/Right Parenthesis", 4);
		
		addMeaning(IDENT, "Identifier");
		addMeaning(NUM_CONST, "Number Constant");
		addMeaning(CHAR_CONST, "Character Constant");
		addMeaning(STRING_CONST, "String Constant");
		
		addMeaning(LEX_ERROR_MALFORM_NUM, "Malformed Number");
		addMeaning(LEX_ERROR_MALFORM_STR, "Malformed String");
		addMeaning(LEX_ERROR_MALFORM_CHR, "Malformed Character");
		addMeaning(LEX_ERROR_INVALID_SYM, "Unknown Symbol(s)");
		addMeaning(LEX_ERROR_COMMENT_END, "Unexpected end of file. Commentary doesn't have an end");
		
		Debug.println("Lexical is Ready");
		init = true;
	}

	private static void addMeaning(int i, String string) {
		meaning_messages.put(i, string);
	}

	private static void addReservedWord(String string, String meaning_message, int type) {
		reserved_words.add(string);
		meaning_messages.put(reserved_words.size() - 1, meaning_message);
	}

}
