package semanthic;

import model.Pair;
import model.Token;
import symbols.ConstantSymbol;
import symbols.SymbolTable;
import syntax_tree.Node;
import syntax_util.SyntaxUtil;

public class ConstantsSemanthicAnalyzer extends SemanthicUtil{
	private SemanthicAnalyzer semanthic;
	private Node constants;
	private SymbolTable globalTable;
	
	public ConstantsSemanthicAnalyzer(SemanthicAnalyzer semanthicAnalyzer, Node constant) {
		super();
		this.semanthic = semanthicAnalyzer;
		this.constants = constant;
		this.globalTable = semanthic.getScopes().get("global-0");
	}
	
	public void analyze() {
		start(constants);
	}
	
	private void start(Node node) {
		ConstantSymbol symbol = new ConstantSymbol();
		Token typeT = node.getChildren().get(0).getChildren().get(0).getToken();
		Token identifer = node.getChildren().get(1).getToken();
		
		int type = getTypeToken(typeT);
		symbol.setToken(identifer);
		symbol.setType(type);
		symbol.setMarkValue(node.getChildren().get(3));
		symbol.setTable(globalTable);
		globalTable.addSymbol(symbol);
		markValueCheck(symbol);
		
		Node n = node.getChildren().get(4);
		if (n.getType() == SyntaxUtil.DECL_CONST_II)
			start(n);
		else if (n.getType() == SyntaxUtil.DECL_CONST_I) {
			middle(n, type);
			if (node.getChildren().size() == 7)
				start(node.getChildren().get(6));
		} else {
			if (node.getChildren().size() == 6)
				start(node.getChildren().get(5));
		}
	}
	
	private void middle(Node node, int type) {
		ConstantSymbol symbol = new ConstantSymbol();
		Token identifer = node.getChildren().get(1).getToken();
		
		symbol.setToken(identifer);
		symbol.setType(type);
		symbol.setMarkValue(node.getChildren().get(3));
		symbol.setTable(globalTable);
		globalTable.addSymbol(symbol);
		markValueCheck(symbol); //Trocar para a variavel não existir durante a verificação de valor
		
		if (node.getChildren().size() == 5)
			middle(node.getChildren().get(4), type);
	}
	
	public static void markValueCheck(ConstantSymbol symbol) {
		Node value = symbol.getValueNode();
		Pair<Integer, Token> type = ValueSemanthicAnalyzer.valueOfExpBool(value, symbol.getTable());
		
		if (type.f >= 0){
			if (type.f != symbol.getType())
				if (!(type.f == INTEGER && symbol.getType() == FLOAT))
					createSemanthicError("Semanthic error on line: " + symbol.getToken().getLine() + ". Incompatible Types, the type of constant " + symbol.getIdentifier() + " is " + getTypeLiteral(symbol.getType()) + " but expression returns " + getTypeLiteral(type.f));
		}
		else
			createProperError(symbol, type);
	}

}
