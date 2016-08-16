package model;

import debug.Debug;

/**
 * 
 * @author João Paulo e Marcelo
 *
 */
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
		else if (lexem.matches(malform_string.pattern()))
			k = new Token(LEX_ERROR_MALFORM_STR, lexem, true);
		else if(lexem.matches(malform_char.pattern()))
			k = new Token(LEX_ERROR_MALFORM_CHR,lexem,true);
			
		return k;
	}

	public static void init() {
		addReservedWord("programa", "palavra_reservada", 0);
		addReservedWord("const", "palavra_reservada", 0);
		addReservedWord("var", "palavra_reservada", 0);
		addReservedWord("funcao", "palavra_reservada", 0);
		addReservedWord("inicio", "palavra_reservada", 0);
		addReservedWord("fim", "palavra_reservada", 0);
		addReservedWord("se", "palavra_reservada", 0);
		addReservedWord("entao", "palavra_reservada", 0);
		addReservedWord("senao", "palavra_reservada", 0);
		addReservedWord("enquanto", "palavra_reservada", 0);
		addReservedWord("faca", "palavra_reservada", 0);
		addReservedWord("leia", "palavra_reservada", 0);
		addReservedWord("escreva", "palavra_reservada", 0);
		addReservedWord("inteiro", "palavra_reservada", 0);
		addReservedWord("real", "palavra_reservada", 0);
		addReservedWord("booleano", "palavra_reservada", 0);
		addReservedWord("verdadeiro", "palavra_reservada", 0);
		addReservedWord("falso", "palavra_reservada", 0);
		addReservedWord("cadeia", "palavra_reservada", 0);
		addReservedWord("caractere", "palavra_reservada", 0);
		
		addReservedWord("+", "operador", 1);
		addReservedWord("-", "operador", 1);
		addReservedWord("*", "operador", 1);
		addReservedWord("/", "operador", 1);
		addReservedWord("<>", "operador", 2);
		addReservedWord("=", "operador", 2);
		addReservedWord("<", "operador", 2);
		addReservedWord("<=", "operador", 2);
		addReservedWord(">", "operador", 2);
		addReservedWord(">=", "operador", 2);
		addReservedWord("nao", "operador", 3);
		addReservedWord("e", "operador", 3);
		addReservedWord("ou", "operador", 3);
		addReservedWord(";", "delimitador", 4);
		addReservedWord(",", "delimitador", 4);
		addReservedWord("(", "delimitador", 4);
		addReservedWord(")", "delimitador", 4);
		
		addMeaning(IDENT, "id");
		addMeaning(NUM_CONST, "nro");
		addMeaning(CHAR_CONST, "caractere");
		addMeaning(STRING_CONST, "cadeia_de_caracteres");
		
		addMeaning(LEX_ERROR_MALFORM_NUM, "nro_mal_formado");
		addMeaning(LEX_ERROR_MALFORM_STR, "cadeia_mal_formada");
		addMeaning(LEX_ERROR_MALFORM_CHR, "caractere_mal_formado");
		addMeaning(LEX_ERROR_INVALID_SYM, "simbolo_invalido");
		addMeaning(LEX_ERROR_COMMENT_END, "comentario_mal_formado");
		
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
