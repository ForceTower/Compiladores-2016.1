package syntax_tree;

import java.util.ArrayList;
import java.util.List;

import model.Token;

public class Node implements Cloneable{
	private List<Node> children;
	private Node father;
	private int info;
	private Token saved;
	
	public Node(Node father, int info, Token saved, List<Node> children) {
		this.children = children;
		this.father = father;
		this.info = info;
		this.saved = saved;
	}
	
	public Node(Node father, int info, Token saved) {
		this(father, info, saved, new ArrayList<>());
	}
	
	public Node(Node father, int info) {
		this(father, info, null, new ArrayList<>());
	}
	
	public Node(int info) {
		this(null, info, null, new ArrayList<>());
	}
	
	public Node(int info, Token saved) {
		this(null, info, saved, new ArrayList<>());
	}
	
	public Node() {
		this(null, -1, null, new ArrayList<>());
	}
	
	public void addChild(Node t) {
		children.add(t);
	}
	
	public void setToken(Token tk) {
		this.saved = tk;
	}
	
	public Token getToken() {
		return saved;
	}
	
	public Node getFather() {
		return father;
	}
	
	public void setFather(Node father) {
		this.father = father;
	}
	
	public boolean isRoot() {
		return father == null;
	}
	
	public boolean isTerminal () {
		return saved != null;
	}
	
	public int getType() {
		return info;
	}
	
	public void setType(int type) {
		this.info = type;
	}
	
	public List<Node> getChildren() {
		return children;
	}
	
	public void print(String prefix, boolean isTail) {
		System.out.println(prefix + (isTail ? " --- " : "|--- ") + (isTerminal() ? saved.getLexem() : "<" + info + ">"));
		
		for (int i = 0; i < children.size() - 1; i++) {
            children.get(i).print(prefix + (isTail ? "    " : "|   "), false);
        }
        if (children.size() > 0) {
            children.get(children.size() - 1).print(prefix + (isTail ?"    " : "|   "), true);
        }
	}
	
	public Node clone() {
		return new Node(info, saved);
	}
	
	public boolean equals(Object o) {
		if (o instanceof Node) {
			if (((Node)o).getType() == info)
				return true;
		}
		return false;
	}
	
	public String toString() {
		return info + ": " + saved;
	}

}
