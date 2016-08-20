package syntax_tree;

public class AbstractSyntaxTree {
	private Node root;
	
	public AbstractSyntaxTree() {
		this(null);
	}
	
	public AbstractSyntaxTree(Node root) {
		this.root = root;
	}
	
	public Node getRoot() {
		return root;
	}
	
	public void setRoot(Node root) {
		this.root = root;
	}
	
	public void print() {
		if (root == null)
			System.out.println("Empty");
		else
			root.print("", true);
	}
}
