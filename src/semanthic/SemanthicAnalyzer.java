package semanthic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import file_io_utils.FileUtils;
import symbols.SymbolTable;
import syntax_tree.AbstractSyntaxTree;

public class SemanthicAnalyzer extends SemanthicConstants{
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

	public void startAnalysis() {
		
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

}
