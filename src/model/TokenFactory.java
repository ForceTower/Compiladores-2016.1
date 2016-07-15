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
		addReservedWord("programa");
		addReservedWord("const");
		addReservedWord("var");
		addReservedWord("funcao");
		addReservedWord("inicio");
		addReservedWord("fim");
		addReservedWord("se");
		addReservedWord("entao");
		addReservedWord("senao");
		addReservedWord("enquanto");
		addReservedWord("faca");
		addReservedWord("leia");
		addReservedWord("escreva");
		addReservedWord("inteiro");
		addReservedWord("real");
		addReservedWord("booleano");
		addReservedWord("verdadeiro");
		addReservedWord("falso");
		addReservedWord("cadeia");
		addReservedWord("caractere");
		
		addReservedWord("+");
		addReservedWord("-");
		addReservedWord("*");
		addReservedWord("/");
		addReservedWord("<>"); //WHATTT?????
		addReservedWord("=");
		addReservedWord("<");
		addReservedWord("<=");
		addReservedWord(">");
		addReservedWord(">=");
		addReservedWord("nao");
		addReservedWord("e");
		addReservedWord("ou");
		addReservedWord(";");
		addReservedWord(",");
		addReservedWord("(");
		addReservedWord(")");
		
		Debug.println("Lexical Fully Ready");
		init = true;
	}

	private static void addReservedWord(String string) {
		reserved_words.add(string);
	}

}
