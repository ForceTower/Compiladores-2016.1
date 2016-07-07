package model;

public class Token extends ConfirmedToken{
	private String lexem;
	private int id;
	
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
	
	public String toString() {
		return "\nTOKEN:\n\tID: " + id + "\n\tLEXEM: " + lexem;
	}

	@Override
	public void confirm() {
		instances.add(this);
	}

}
