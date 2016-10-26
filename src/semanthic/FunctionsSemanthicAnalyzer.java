package semanthic;

import model.Token;
import symbols.FunctionSymbol;
import symbols.Symbol;
import symbols.SymbolTable;
import symbols.VariableSymbol;
import syntax_tree.Node;
import syntax_util.SyntaxUtil;

public class FunctionsSemanthicAnalyzer extends SemanthicUtil {
	private SemanthicAnalyzer semanthic;
	private Node functions;
	private SymbolTable globalTable;

	public FunctionsSemanthicAnalyzer(SemanthicAnalyzer semanthicAnalyzer, Node functions) {
		super();
		this.semanthic = semanthicAnalyzer;
		this.functions = functions;
		this.globalTable = semanthic.getScopes().get("global-0");
	}
	
	public void analyze() {
		start(functions);
	}
	
	private void start(Node node) {
		Node n = node.getChildren().get(1);
		middle(n);
		if (node.getChildren().size() == 3)
			start(node.getChildren().get(2));
	}
	
	private void middle(Node node) {
		Token typeT = null;
		Token identifier;
		FunctionSymbol symbol = new FunctionSymbol();
		int index;
		Node one = node.getChildren().get(0);
		if (one.isTerminal()) {
			identifier = one.getToken();
			index = 1;
		} else {
			typeT = one.getChildren().get(0).getToken(); 
			identifier = node.getChildren().get(1).getToken();
			index = 2;
		}
		
		int type;
		if (typeT == null)
			type = VOID;
		else
			type = getTypeToken(typeT);
		
		symbol.setToken(identifier);
		symbol.setType(type);
		symbol.setTable(globalTable);
		
		index++;
		if (node.getChildren().get(index).isTerminal()) {
			symbol.setParamMark(null);
		} else {
			symbol.setParamMark(node.getChildren().get(index));
			index++;
		}
		
		index++;
		index++;
		
		if (node.getChildren().get(index).isTerminal()) 
			symbol.setBodyMark(null);
		
		else {
			Node bc = node.getChildren().get(index);
			if (bc.getType() == SyntaxUtil.RETORNO_FUNC) {
				symbol.setBodyMark(null);
				symbol.setReturnMark(bc);
			} else {
				symbol.setBodyMark(node.getChildren().get(index));
				index++;
				
				if (type != VOID) symbol.setReturnMark(node.getChildren().get(index));
			}
		}
		
		if (globalTable.addSymbol(symbol)) { //if this is false.. we have a incorrect function , what should we do with it?
			SymbolTable mTable = new SymbolTable(symbol.getIdentifier());
			semanthic.getScopes().put(symbol.getIdentifier(), mTable);
			symbol.setScope(mTable);
			if (symbol.getParamMark() != null) 
				paramMarkCheck(symbol);
		}
	}

	private void paramMarkCheck(FunctionSymbol symbol) {
		Node param = symbol.getParamMark();
		if (param == null) return;
		
		SymbolTable table = semanthic.getScopes().get(symbol.getIdentifier());
		paramMarkCheckStart(table, param, symbol);
	}

	private void paramMarkCheckStart(SymbolTable table, Node node, FunctionSymbol function) {
		VariableSymbol symbol = new VariableSymbol();
		
		Token typeT = node.getChildren().get(0).getChildren().get(0).getToken();
		int type = getTypeToken(typeT);
		Token identifier;
		Node arrayMark = null;
		boolean isArray = false;
		
		Node n = node.getChildren().get(1);
		if (n.isTerminal())
			identifier = n.getToken();
		else {
			isArray = true;
			arrayMark = n;
			identifier = node.getChildren().get(2).getToken();
		}
		
		int index;
		if (isArray) index = 3;
		else index = 2;
		
		symbol.setArray(isArray);
		symbol.setIndexMark(arrayMark);
		symbol.setType(type);
		symbol.setToken(identifier);
		
		if (isArray) {
			VariablesSemanthicAnalyzer.indexArrayCheck(symbol);
			VariablesSemanthicAnalyzer.indexArrayCount(symbol);
		}
		
		boolean exists = false;
		Symbol s = globalTable.getSymbols().get(identifier.getLexem());
		if (s instanceof FunctionSymbol) exists = true;
		
		if (exists) createSemanthicError(identifier.getLine(), "Na linha: " + identifier.getLine() + ". O identificador " + symbol.getIdentifier() + " já foi declarado como uma função");
		else table.addSymbol(symbol);
		
		function.addParam(symbol);
		
		if (node.getChildren().size() == index + 1) {
			paramMarkCheckMiddle(table, node.getChildren().get(index), function);
		}
	}

	private void paramMarkCheckMiddle(SymbolTable table, Node node, FunctionSymbol function) {
		VariableSymbol symbol = new VariableSymbol();
		Token typeT = node.getChildren().get(1).getChildren().get(0).getToken();
		int type = getTypeToken(typeT);
		Token identifier;
		Node arrayMark = null;
		boolean isArray = false;
		
		Node n = node.getChildren().get(2);
		if (n.isTerminal()) 
			identifier = n.getToken();
		else {
			isArray = true;
			arrayMark = n;
			identifier = node.getChildren().get(3).getToken();
		}
		
		int index;
		if (isArray) index = 4;
		else index = 3;
		
		symbol.setArray(isArray);
		symbol.setIndexMark(arrayMark);
		symbol.setType(type);
		symbol.setToken(identifier);
		
		if (isArray) {
			VariablesSemanthicAnalyzer.indexArrayCheck(symbol);
			VariablesSemanthicAnalyzer.indexArrayCount(symbol);
		}
		
		boolean exists = false;
		Symbol s = globalTable.getSymbols().get(identifier.getLexem());
		if (s instanceof FunctionSymbol) exists = true;
		
		if (exists) createSemanthicError(identifier.getLine(), "Na linha: " + identifier.getLine() + ". O identificador " + symbol.getIdentifier() + " já foi declarado como uma função");
		else table.addSymbol(symbol);
		
		function.addParam(symbol);
		
		if (node.getChildren().size() == index + 1) {
			paramMarkCheckMiddle(table, node.getChildren().get(index), function);
		}
	}

}
