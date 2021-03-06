package model;


public class Token {
	private String lexem;
	private int id;
	private int line;
	private int position;
	@SuppressWarnings("unused")
	private boolean detailed;
	private boolean malformed;
	private boolean errorToken;
	private int type;
	
	public Token() {
		this(-1, "");
	}
	
	public Token(String lexem) {
		this(-1, lexem);
	}
	
	public Token(int id) {
		this(id, "");
	}
	
	public Token(int id, String lexem) {
		this.id = id;
		this.lexem = lexem;
		this.malformed = false;
	}
	
	public Token(int id, String lexem, boolean malformed) {
		this.id = id;
		this.lexem = lexem;
		this.malformed = malformed;
	}
	
	public Token (int id, String lexem, int line, int position) {
		this.id = id;
		this.lexem = lexem;
		this.line = line;
		this.position = position;
		this.malformed = false;
	}

	public String getLexem() {
		return lexem;
	}

	public void setLexem(String lexem) {
		this.lexem = lexem;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getLine() {
		return line;
	}
	
	public void setLine(int line) {
		this.line = line;
	}

	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public boolean isMalformed() {
		return malformed;
	}
	
	public void setMalformed(boolean b) {
		malformed = b;
	}
	
	public String detailedToString() {
		if (errorToken)
			return "<- TOKEN_ERR\tID: " + id + "\tLexeme: " + lexem + "\tINFO: " + TokenFactory.meaning_messages.get(id) + "\tLine: " + line + "\tPosition: " + position + " ->";
		return "<- TOKEN\tID: " + id + "\tLexeme: " + lexem + "\tINFO: " + TokenFactory.meaning_messages.get(id) + "\tLine: " + line + "\tPosition: " + position + " ->";
	}
	
	public String toString() {
		/*if (detailed)
			return detailedToString();
		if (errorToken)
			return "<-TOKEN_ERR\tID: " + id + "\tLexeme: " + lexem + "\tINFO: " + TokenFactory.meaning_messages.get(id) + " ->";
		return "<- TOKEN\tID: " + id + "\tLexeme: " + lexem + "\tINFO: " + TokenFactory.meaning_messages.get(id) + " ->";*/
		return line + " " + lexem + " " + TokenFactory.meaning_messages.get(id);
	}

	public void showDetails(boolean b) {
		detailed = b;
	}

	public void errorToken(boolean b) {
		errorToken = b;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}

}
