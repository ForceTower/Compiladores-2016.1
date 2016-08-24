package model;

import debug.Debug;

/**
 * 
 * @author João Paulo e Marcelo
 *
 */
public class TokenFactory implements TokenConstants{
	private static boolean init = false;
	
	public static synchronized Token findToken(String lexem) {
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
		
		if (k != null && k.getId() < 70) 
			k.setType(types.get(k.getLexem()));
		
		return k;
	}

	public static synchronized void init() {
		addReservedWord("programa", "programa - palavra_reservada", 0);
		addReservedWord("const", "const - palavra_reservada", 0);
		addReservedWord("var", "var - palavra_reservada", 0);
		addReservedWord("funcao", "funcao - palavra_reservada", 0);
		addReservedWord("inicio", "inicio - palavra_reservada", 0);
		addReservedWord("fim", "fim - palavra_reservada", 0);
		addReservedWord("se", "se - palavra_reservada", 0);
		addReservedWord("entao", "entao - palavra_reservada", 0);
		addReservedWord("senao", "senao - palavra_reservada", 0);
		addReservedWord("enquanto", "enquanto - palavra_reservada", 0);
		addReservedWord("faca", "faca - palavra_reservada", 0);
		addReservedWord("leia", "leia - palavra_reservada", 0);
		addReservedWord("escreva", "escreva - palavra_reservada", 0);
		addReservedWord("inteiro", "inteiro - tipo", 1);
		addReservedWord("real", "real - tipo", 1);
		addReservedWord("booleano", "booleano - tipo", 1);
		addReservedWord("verdadeiro", "verdadeiro - palavra_reservada", 0);
		addReservedWord("falso", "falso - palavra_reservada", 0);
		addReservedWord("cadeia", "cadeia - tipo", 0);
		addReservedWord("caractere", "caractere - palavra_reservada", 0);
		
		addReservedWord("+", "Operador Aritmetico (+)", 2);
		addReservedWord("-", "Operador Aritmetico (-)", 2);
		addReservedWord("*", "Operador Aritmetico (*)", 2);
		addReservedWord("/", "Operador Aritmetico (/)", 2);
		addReservedWord("<>", "Operador Logico (<>)", 3);
		addReservedWord("=", "Atribuicao (=)", 3);
		addReservedWord("<", "Operador Logico (<)", 3);
		addReservedWord("<=", "Operador Logico (<=)", 3);
		addReservedWord(">", "Operador Logico (>)", 3);
		addReservedWord(">=", "Operador Logico (>=)", 3);
		addReservedWord("nao", "Operador Logico (nao)", 4);
		addReservedWord("e", "Operador Logico", 4);
		addReservedWord("ou", "Operador Logico", 4);
		addReservedWord(";", "Ponto-e-Virgula (;)", 5);
		addReservedWord(",", "Virgula(,)", 5);
		addReservedWord("(", "Abre Parenteses", 5);
		addReservedWord(")", "Fecha Parenteses", 5);
		
		addMeaning(IDENT, "Identificador");
		addMeaning(NUM_CONST, "Numero");
		addMeaning(CHAR_CONST, "Constante Caracteres");
		addMeaning(STRING_CONST, "Constante Cadeia de Caracteres");
		
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
		types.put(string, i);
	}

	private static void addReservedWord(String string, String meaning_message, int type) {
		reserved_words.add(string);
		meaning_messages.put(reserved_words.size() - 1, meaning_message);
		types.put(string, type);
	}

}
