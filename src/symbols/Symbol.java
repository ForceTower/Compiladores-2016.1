package symbols;

import model.Token;

public class Symbol {
	private Token identifierToken;
	private int type;
	
	public Symbol() {
		type = -1;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public void setToken(Token t) {
		this.identifierToken = t;
	}
	
	public String getIdentifier() {
		return identifierToken.getLexem();
	}
	
	public Token getToken() {
		return identifierToken;
	}
	
	public String getTypeLiteral() {
		if (type == 0)
			return "Integer";
		else if (type == 1)
			return "Float";
		else if (type == 2)
			return "Character";
		else if (type == 3)
			return "String";
		else if (type == 4)
			return "Boolean";
		else if (type == 5)
			return "Void";
		else
			return "No type assigned";
	}
	
	public String toString() {
		return "Identifier: " + getIdentifier() + "\t Type: " + getTypeLiteral();
	}

}