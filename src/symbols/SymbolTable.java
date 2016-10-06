package symbols;

import java.util.Hashtable;

import semanthic.SemanthicUtil;
import symbols.Symbol;

public class SymbolTable {
	private String name;
	private Hashtable<String, Symbol> symbols;
	
	public SymbolTable(String name) {
		this.name = name;
		symbols = new Hashtable<>();
	}
	
	public String getName() {
		return name;
	}
	
	public boolean addSymbol(Symbol sym) {
		if (symbols.get(sym.getIdentifier()) != null) {
			SemanthicUtil.createSemanthicError("On line: " + sym.getToken().getLine() + ". Identifier " + sym.getIdentifier() + " already declared");
			return false;
		}
		
		symbols.put(sym.getIdentifier(), sym);
		return true;
	}
	
	public Hashtable<String, Symbol> getSymbols() {
		return symbols;
	}

}
