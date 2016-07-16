package model;

public class TokenErr extends Token {

	public TokenErr() {
		super();
	}

	public TokenErr(int id, String lexem, int line, int position) {
		super(id, lexem, line, position);
	}

	public TokenErr(int id, String lexem) {
		super(id, lexem);
	}

	public TokenErr(int id) {
		super(id);
	}

	public TokenErr(String lexem) {
		super(lexem);
	}
	

}
