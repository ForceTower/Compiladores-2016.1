package semanthic;

import model.Token;
import symbols.FunctionSymbol;
import symbols.Symbol;
import symbols.SymbolTable;
import symbols.VariableSymbol;
import syntax_tree.Node;
import syntax_util.SyntaxUtil;

public class VariablesSemanthicAnalyzer extends SemanthicUtil{
	private SemanthicAnalyzer semanthic;
	private Node variable;
	private SymbolTable table;

	public VariablesSemanthicAnalyzer(SemanthicAnalyzer semanthicAnalyzer, Node variable, SymbolTable table) {
		super();
		this.semanthic = semanthicAnalyzer;
		this.variable = variable;
		this.table = table;
	}
	
	public void analyze() {
		start(variable);
	}
	
	private void start(Node node) {
		VariableSymbol symbol = new VariableSymbol();
		
		boolean isArray = false;
		Token identifier;
		Token typeT = node.getChildren().get(0).getChildren().get(0).getToken();
		Node indexMark = null;
		Node n = node.getChildren().get(1);
		
		if (!n.isTerminal()) { 
			isArray = true; 
			indexMark = n;
			identifier = node.getChildren().get(2).getToken();
		} else {
			identifier = node.getChildren().get(1).getToken();
		}
		
		int type = getTypeToken(typeT);
		symbol.setType(type);
		symbol.setToken(identifier);
		symbol.setArray(isArray);
		symbol.setIndexMark(indexMark);
		if (isArray) {
			indexArrayCheck(symbol);
			indexArrayCount(symbol);
		}
		
		boolean exists = false;
		Symbol s = semanthic.getScopes().get("global-0").getSymbols().get(identifier.getLexem());
		if (s instanceof FunctionSymbol) exists = true;
		
		if (!exists) table.addSymbol(symbol);
		else createSemanthicError("Semanthic error on line " + identifier.getLine() + ". The identifier " + symbol.getIdentifier() + " was already declared as a function");
		
		int index;
		if (isArray)
			index = 3;
		else
			index = 2;
		
		Node l = node.getChildren().get(index);
		if (l.getType() == SyntaxUtil.DECL_VAR_II)
			start(l);
		else if (l.getType() == SyntaxUtil.DECL_VAR_I) {
			middle(l, type);
			index++; //;
			index++; //get to the production
			if (node.getChildren().size() == index + 1) {
				start(node.getChildren().get(index));
			}
		} else {
			index++; //;
			if (node.getChildren().size() == index + 1)
				start(node.getChildren().get(index));
		}
	}
	
	private void middle(Node node, int type) {
		VariableSymbol symbol = new VariableSymbol();
		Token identifier;
		boolean isArray = false;
		Node indexMark = null;
		Node n = node.getChildren().get(1);
		if (!n.isTerminal()) { 
			isArray = true; 
			indexMark = n;
			identifier = node.getChildren().get(2).getToken();
		} else {
			identifier = node.getChildren().get(1).getToken();
		}
		
		symbol.setType(type);
		symbol.setToken(identifier);
		symbol.setArray(isArray);
		symbol.setIndexMark(indexMark);
		if (isArray) {
			indexArrayCheck(symbol);
			indexArrayCount(symbol);
		}
		
		boolean exists = false;
		Symbol s = semanthic.getScopes().get("global-0").getSymbols().get(identifier.getLexem());
		if (s instanceof FunctionSymbol) exists = true;
		
		if (!exists) table.addSymbol(symbol);
		else createSemanthicError("Semanthic error on line " + identifier.getLine() + ". The identifier " + symbol.getIdentifier() + " was already declared as a function");
		
		int index;
		if (isArray)
			index = 3;
		else
			index = 2;
		
		if (node.getChildren().size() == index + 1) {
			Node l = node.getChildren().get(index);
			if (l.getType() == SyntaxUtil.DECL_VAR_I)
				middle(l, type);
		}
	}

	public static void indexArrayCheck(VariableSymbol symbol) {
		
	}
	
	public static void indexArrayCount(VariableSymbol symbol) {
		
	}

}
