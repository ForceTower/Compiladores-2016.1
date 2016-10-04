package symbols;

import java.util.ArrayList;
import java.util.List;

import syntax_tree.Node;

public class FunctionSymbol extends Symbol{
	private Node paramMark;
	private Node bodyMark;
	private Node returnMark;
	private List<Symbol> params;
	
	public FunctionSymbol() {
		super();
		params = new ArrayList<>();
	}

	public void setParamMark(Node param) {
		this.paramMark = param;
	}
	
	public Node getParamMark() {
		return paramMark;
	}

	public void setBodyMark(Node body) {
		this.bodyMark = body;
	}
	
	public Node getBodyMark() {
		return bodyMark;
	}

	public void setReturnMark(Node ret) {
		this.returnMark = ret;
	}
	
	public Node getReturnMark() {
		return returnMark;
	}
	
	public void addParam(Symbol sym) {
		params.add(sym);
	}
	
	public List<Symbol> getParams() {
		return params;
	}

	public int getParamQuantity() {
		return params.size();
	}
	
	public String toString() {
		return "Function: " + super.toString() + " with " + getParamQuantity() + " params";
	}

}
