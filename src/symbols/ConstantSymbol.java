package symbols;

import syntax_tree.Node;

public class ConstantSymbol extends Symbol{
	private Node valueNode;
	
	public ConstantSymbol() {
		super();
	}

	public void setMarkValue(Node node) {
		this.valueNode = node;
	}
	
	public Node getValueNode() {
		return valueNode;
	}
	
	public String toString() {
		return "Constant: " + super.toString();
	}

}
