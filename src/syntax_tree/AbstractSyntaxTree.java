package syntax_tree;

import java.util.ArrayList;
import java.util.List;

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
	
	public AbstractSyntaxTree normalize() {
		Node nRoot = root;
		norms(nRoot);
		/*AbstractSyntaxTree ast = new AbstractSyntaxTree();
		ast.setRoot(new Node());
		norms2(ast.getRoot(), root);
		Node root2 = ast.getRoot().getChildren().get(0).getChildren().get(0);
		root2.setFather(null);
		ast.setRoot(root2);*/
		return this;
	}

	public void norms(Node node) {
		if (node.isTerminal())
			return;
		List<Node> remove = new ArrayList<>();
		for (Node n : node.getChildren()) {
			if (!n.isTerminal() && n.getChildren().size() == 0)
				remove.add(n);
			else
				norms(n);
		}
		
		node.getChildren().removeAll(remove);
	}
	
	public void norms2(Node cur, Node prin) {
		while (prin.getChildren().size() == 1)
			prin = prin.getChildren().get(0);
		
		if (prin.isTerminal()) {
			Node nt = prin.clone();
			nt.setFather(cur);
			cur.addChild(nt);
		} else if (prin.getChildren().size() > 1) {
			Node nt = prin.clone();
			nt.setFather(cur);
			cur.addChild(nt);
			for (Node n : prin.getChildren())
				norms2(nt, n);
		}
	}
}
