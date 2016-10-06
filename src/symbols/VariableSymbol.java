package symbols;

import syntax_tree.Node;

public class VariableSymbol extends Symbol{
	private boolean isArray;
	private Node indexMark;
	private int dimensionQuant;
	
	public VariableSymbol() {
		super();
	}

	public void setArray(boolean isArray) {
		this.isArray = isArray;
	}
	
	public boolean isArray() {
		return isArray;
	}

	public void setIndexMark(Node n) {
		this.indexMark = n;
	}
	
	public Node getIndexMark() {
		return indexMark;
	}
	
	public void setDimensionQuantity(int i) {
		this.dimensionQuant = i;
	}
	
	public int getDimensionQuantity() {
		return dimensionQuant;
	}
	
	public String toString() {
		if (isArray)
			return "Variable: " + super.toString() + " with " + getDimensionQuantity() + " dimensions";
		else
			return "Variable: " + super.toString();
	}

}
