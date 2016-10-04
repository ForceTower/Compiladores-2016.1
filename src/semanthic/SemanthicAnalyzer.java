package semanthic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import file_io_utils.FileUtils;
import symbols.FunctionSymbol;
import symbols.Symbol;
import symbols.SymbolTable;
import syntax_tree.AbstractSyntaxTree;
import syntax_tree.Node;
import syntax_util.SyntaxUtil;

public class SemanthicAnalyzer extends SemanthicUtil{
	private static SemanthicAnalyzer instance;
	private Hashtable<String, SymbolTable> scopes;
	private AbstractSyntaxTree ast;
	private File result;
	private List<String> errors;
	
	public static SemanthicAnalyzer get() {
		return instance;
	}

	public SemanthicAnalyzer(AbstractSyntaxTree ast, File result) {
		super();
		this.ast = ast;
		this.result = result;
		this.errors = new ArrayList<>();
		this.scopes = new Hashtable<>();
		this.scopes.put("global-0", new SymbolTable("global-0"));
		
		instance = this;
	}

	public void startAnalysis() throws IOException {
		declareConstants();
		declareVariables();
		declareFunctions();
		declareMain();
		bodyBuilder();
		writeErrors();
		//showAll();
	}
	
	public Symbol lookup(String id, String table) {
		if (!table.equals("global-0")) {
			SymbolTable tab = scopes.get(table);
			if (tab == null) return null;
			
			Symbol sbl = tab.getSymbols().get(id);
			if (sbl != null) return sbl;
		}
		SymbolTable tab = scopes.get("global-0");
		return tab.getSymbols().get(id);
	}

	public Hashtable<String, SymbolTable> getScopes() {
		return scopes;
	}
	
	public List<String> getErrorsList() {
		return errors;
	}
	
	public void writeErrors() throws IOException {
		FileUtils fu = new FileUtils(result);
		if (errors.size() == 0) fu.writeString("Sucesso!");
		else for (String str : errors) fu.writeString(str);
	}
	
	private void declareConstants() {
		Node constant = ast.getNode(SyntaxUtil.DECL_CONST_CONTINUO);
		if (constant == null) return;
		
		ConstantsSemanthicAnalyzer csa = new ConstantsSemanthicAnalyzer(this, constant);
		csa.analyze();
	}

	private void declareVariables() {
		Node variable = ast.getNode(SyntaxUtil.DECL_VAR_CONTINUO);
		if (variable == null || variable.getFather().getFather().getType() == SyntaxUtil.COMANDOS) return;
		
		VariablesSemanthicAnalyzer vsa = new VariablesSemanthicAnalyzer(this, variable, scopes.get("global-0"));
		vsa.analyze();
	}

	private void declareFunctions() {
		Node function = ast.getNode(SyntaxUtil.DECL_FUNC);
		if (function == null) return;
		
		FunctionsSemanthicAnalyzer fsa = new FunctionsSemanthicAnalyzer(this, function);
		fsa.analyze();
	}
	
	private void declareMain() {
		Node main = ast.getNode(SyntaxUtil.DECL_MAIN);
		SymbolTable globalTable = scopes.get("global-0");
		FunctionSymbol symbol = new FunctionSymbol();
		symbol.setBodyMark(main.getChildren().get(2));
		symbol.setType(VOID);
		symbol.setToken(main.getChildren().get(0).getToken());
		globalTable.addSymbol(symbol);
		
		scopes.put(symbol.getIdentifier(), new SymbolTable(symbol.getIdentifier()));
	}

	private void bodyBuilder() {
		
	}
	
	public void showAll() {
		for (SymbolTable table : scopes.values()) {
			System.out.println("\nTable: - " + table.getName() + " -");
			for (Symbol symbol : table.getSymbols().values())
				System.out.println(symbol);
		}
	}

}
