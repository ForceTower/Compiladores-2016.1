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
			SemanthicUtil.createSemanthicError(sym.getToken().getLine(), "Na linha: " + sym.getToken().getLine() + ". Identificador " + sym.getIdentifier() + " já foi declarado");
			return false;
		}
		
		symbols.put(sym.getIdentifier(), sym);
		return true;
	}
	
	public Hashtable<String, Symbol> getSymbols() {
		return symbols;
	}

}
