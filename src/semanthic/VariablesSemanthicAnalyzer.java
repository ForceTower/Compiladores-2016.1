package semanthic;

import model.Pair;
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
		symbol.setTable(table);
		if (isArray) {
			indexArrayCheck(symbol);
			indexArrayCount(symbol);
		}
		
		boolean exists = false;
		Symbol s = semanthic.getScopes().get("global-0").getSymbols().get(identifier.getLexem());
		if (s instanceof FunctionSymbol) exists = true;
		
		if (!exists) table.addSymbol(symbol);
		else createSemanthicError("On line " + identifier.getLine() + ". The identifier " + symbol.getIdentifier() + " was already declared as a function");
		
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
		symbol.setTable(table);
		if (isArray) {
			indexArrayCheck(symbol);
			indexArrayCount(symbol);
		}
		
		boolean exists = false;
		Symbol s = semanthic.getScopes().get("global-0").getSymbols().get(identifier.getLexem());
		if (s instanceof FunctionSymbol) exists = true;
		
		if (!exists) table.addSymbol(symbol);
		else createSemanthicError("On line " + identifier.getLine() + ". The identifier " + symbol.getIdentifier() + " was already declared as a function");
		
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
		Node node = symbol.getIndexMark();
		if (node == null) return;
		
		if (node.getChildren().contains(new Node(SyntaxUtil.ARRAY_INDEXES_OPT)) || node.getChildren().contains(new Node(SyntaxUtil.EXPR_SIMPLES))) {
			Node exp_simp = null;
			
			int index = node.getChildren().indexOf(new Node(SyntaxUtil.EXPR_SIMPLES));
			if (index == -1) {
				index = node.getChildren().indexOf(new Node(SyntaxUtil.ARRAY_INDEXES_OPT));
				exp_simp = node.getChildren().get(index);
			} else {
				exp_simp = node.getChildren().get(index);
			}
			
			Pair<Integer, Token> type = ValueSemanthicAnalyzer.valueOfExpSimple(exp_simp, symbol.getTable());
			if (type.f != INTEGER) {
				if (type.f > 0)
					createSemanthicError("On line: " + symbol.getToken().getLine() + ". The expression at index number " + 1 + " of array " + symbol.getIdentifier() + " is a " + getTypeLiteral(type.f) + ", but it must be an integer to be an index");
				else
					createProperError(symbol, type);
			}
		}
		
		if (node.getChildren().contains(new Node(SyntaxUtil.ARRAY_I)) || node.getChildren().contains(new Node(SyntaxUtil.ARRAY_PARAM_I))) {
			int index = node.getChildren().indexOf(new Node(SyntaxUtil.ARRAY_I));
			if (index == -1)
				index = node.getChildren().indexOf(new Node(SyntaxUtil.ARRAY_PARAM_I));
			
			indexArrayCheckMiddle(symbol, node.getChildren().get(index), 2);
		}
	}
	
	private static void indexArrayCheckMiddle(VariableSymbol symbol, Node node, int i) {
		if (node.getChildren().contains(new Node(SyntaxUtil.ARRAY_INDEXES_OPT)) || node.getChildren().contains(new Node(SyntaxUtil.EXPR_SIMPLES))) {
			Node exp_simp = null;
			
			int index = node.getChildren().indexOf(new Node(SyntaxUtil.EXPR_SIMPLES));
			if (index == -1) {
				index = node.getChildren().indexOf(new Node(SyntaxUtil.ARRAY_INDEXES_OPT));
				exp_simp = node.getChildren().get(index);
			} else {
				exp_simp = node.getChildren().get(index);
			}
			
			Pair<Integer, Token> type = ValueSemanthicAnalyzer.valueOfExpSimple(exp_simp, symbol.getTable());
			if (type.f != INTEGER) {
				if (type.f > 0)
					createSemanthicError("On line: " + symbol.getToken().getLine() + ". The expression at index number " + i + " of array " + symbol.getIdentifier() + " is a " + getTypeLiteral(type.f) + ", but it must be an Integer to become an index");
				else
					createProperError(symbol, type);
			}
		}
		
		if (node.getChildren().contains(new Node(SyntaxUtil.ARRAY_I)) || node.getChildren().contains(new Node(SyntaxUtil.ARRAY_PARAM_I))) {
			int index = node.getChildren().indexOf(new Node(SyntaxUtil.ARRAY_I));
			if (index == -1)
				index = node.getChildren().indexOf(new Node(SyntaxUtil.ARRAY_PARAM_I));
			
			indexArrayCheckMiddle(symbol, node.getChildren().get(index), i + 1);
		}
	}

	public static int indexArrayCount(VariableSymbol symbol) {
		Node node = symbol.getIndexMark();
		if (node == null) return 0;
		
		if (!node.getChildren().contains(new Node(SyntaxUtil.ARRAY_I)) && !node.getChildren().contains(new Node(SyntaxUtil.ARRAY_PARAM_I))) {
			symbol.setDimensionQuantity(1);
			return 1;
		}
		
		for (Node n : node.getChildren())
			if (n.getType() == SyntaxUtil.ARRAY_I || n.getType() == SyntaxUtil.ARRAY_PARAM_I)
				return indexArrayCountMiddle(symbol, 1, n);
		
		symbol.setDimensionQuantity(1);
		return 1;
	}

	private static int indexArrayCountMiddle(VariableSymbol symbol, int i, Node node) {
		if (node.getChildren().contains(new Node(SyntaxUtil.ARRAY_I)) || node.getChildren().contains(new Node(SyntaxUtil.ARRAY_PARAM_I)))
			for (Node n : node.getChildren())
				if (n.getType() == SyntaxUtil.ARRAY_I || n.getType() == SyntaxUtil.ARRAY_PARAM_I)
					return indexArrayCountMiddle(symbol, i + 1, n);
		
		symbol.setDimensionQuantity(i + 1);
		return i + 1;
	}

}
