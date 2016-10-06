package semanthic;

import java.util.ArrayList;
import java.util.List;

import model.Pair;
import model.Token;
import model.TokenFactory;
import symbols.FunctionSymbol;
import symbols.Symbol;
import symbols.SymbolTable;
import symbols.VariableSymbol;
import syntax_tree.Node;
import syntax_util.SyntaxUtil;

public class BodySemanthicAnalyzer extends SemanthicUtil{
	private SemanthicAnalyzer semanthic;
	private FunctionSymbol function;

	public BodySemanthicAnalyzer(SemanthicAnalyzer semanthicAnalyzer, FunctionSymbol function) {
		super();
		this.semanthic = semanthicAnalyzer;
		this.function = function;
	}
	
	public void analyze() {
		body(function.getBodyMark());
		funcReturn(function.getReturnMark());
	}

	private void body(Node body) {
		if (body == null) return;
		if (body.getChildren().size() >= 1) {
			Node work = body.getChildren().get(0);
			
			if (work.getChildren().get(0).isTerminal()) {
				Token tk = work.getChildren().get(0).getToken();
				if (tk.getId() == TokenFactory.IDENT) {
					callAttr(work);
				} else if (tk.getId() == SyntaxUtil.getTokenId("<")) {
					attribuition(work);
				} else if (tk.getId() == SyntaxUtil.getTokenId("leia")) {
					read(work);
				} else if (tk.getId() == SyntaxUtil.getTokenId("escreva")) {
					write(work);
				} else if (tk.getId() == SyntaxUtil.getTokenId("enquanto")) {
					whiled(work);
				} else if (tk.getId() == SyntaxUtil.getTokenId("inicio")) {
					scope(work);
				} else if (tk.getId() == SyntaxUtil.getTokenId("se")) {
					ifed(work);
				}
			} else {
				var(work.getChildren().get(0));
			}
		}
		if (body.getChildren().size() == 2)
			body(body.getChildren().get(1));
		
	}

	private void callAttr(Node work) {
		if (work.getChildren().get(1).getChildren().get(0).getToken().getId() == SyntaxUtil.getTokenId("=")) {
			attribuition(work);
		} else {
			funcCall(work, function.getFunctionScope());
		}
	}

	private void attribuition(Node work) {
		boolean allright = false;
		int index = 0;
		int indexQtt = 0;
		boolean arrayAccess = false;
		Pair<Integer, Token> type;
		
		int continuation = work.getChildren().indexOf(new Node(TokenFactory.IDENT));
		Token id = work.getChildren().get(continuation).getToken();
		if (work.getChildren().get(0).getToken().getId() == SyntaxUtil.getTokenId("<")) {
			arrayAccess = true;
			indexQtt = 1;
			index++;
			index++;
			VariableSymbol vs = new VariableSymbol();
			vs.setToken(id);
			ValueSemanthicAnalyzer.checkIndexes(work, function.getFunctionScope(), vs);
			
			index = work.getChildren().indexOf(new Node(SyntaxUtil.ARRAY_I));
			if (index != -1)
				indexQtt = ValueSemanthicAnalyzer.getQuantityOf(work.getChildren().get(index), 1);
			
			type = ValueSemanthicAnalyzer.valueOfExpBool(work.getChildren().get(continuation+2), function.getFunctionScope());
			
		} else {
			work = work.getChildren().get(continuation+1);
			type = ValueSemanthicAnalyzer.valueOfExpBool(work.getChildren().get(1), function.getFunctionScope());
		}
		
		Symbol symbol = semanthic.lookup(id.getLexem(), function.getFunctionScope().getName());
		if (symbol != null) {
			if (!(symbol instanceof VariableSymbol)) {
				createSemanthicError("On line: " + id.getLine() + ". The value of identifier " + id.getLexem() + " can not be changed");
			} else {
				VariableSymbol var = (VariableSymbol)symbol;
				allright = true;
				if (arrayAccess && !var.isArray()) {
					createSemanthicError("On line: " + id.getLine() + ". The variable " + id.getLexem() + " is not an array");
				} else if (arrayAccess && var.isArray()) {
					if (indexQtt != var.getDimensionQuantity()) {
						createSemanthicError("On line: " + id.getLine() + ". The variable " + id.getLexem() + " has " + var.getDimensionQuantity() + " dimensions, but you informed " + indexQtt);
					}
				}
			}
		} else {
			createSemanthicError("On line: " + id.getLine() + ". The variable " + id.getLexem() + " was not declared");
		}
		
		if (allright && type.f >= 0) {
			if (symbol.getType() != type.f) {
				if (!(symbol.getType() == FLOAT && type.f == INTEGER))
					createSemanthicError("On line: " + type.s.getLine() + ". Incompatible types, identifier is " + getTypeLiteral(symbol.getType()) + " and expression is " + getTypeLiteral(type.f));
			}
		} else if (type.f < 0) {
			createProperError(function, type);
		}
	}

	private void read(Node work) {
		int index = work.getChildren().indexOf(new Node(TokenFactory.IDENT));
		Token tk = work.getChildren().get(index).getToken();
		
		Symbol symbol = semanthic.lookup(tk.getLexem(), function.getFunctionScope().getName());
		boolean exists = false;
		if (symbol != null) {
			if (symbol instanceof VariableSymbol) {
				exists = true;
			} else {
				createSemanthicError("On line: " + tk.getLine() + ". The value of " + tk.getLexem() + " cannot be changed, the identifier is declared as function or constant");
			}
		} else {
			createSemanthicError("On line: " + tk.getLine() + ". The variable " + tk.getLexem() + " was not declared");
		}
		int qttIndex = 0;
		index = work.getChildren().indexOf(new Node(SyntaxUtil.ARRAY));
		if (index != -1) {
			VariableSymbol var;
			if (exists) {
				var = (VariableSymbol)symbol;
			} else {
				var = new VariableSymbol();
				var.setToken(tk);
			}
			
			Node array = work.getChildren().get(index);
			ValueSemanthicAnalyzer.checkIndexes(array, function.getFunctionScope(), var);
			qttIndex = 1;
			int nIndex = array.getChildren().indexOf(new Node(SyntaxUtil.ARRAY_I));
			if (nIndex != -1) {
				Node array_i = array.getChildren().get(nIndex);
				qttIndex = ValueSemanthicAnalyzer.getQuantityOf(array_i, qttIndex);
			}
		}
		
		if (exists) {
			VariableSymbol var = (VariableSymbol)symbol;
			if (var.isArray() && qttIndex == 0) {
				createSemanthicError("On line: " + tk.getLine() + ". The variable " + tk.getLexem() + " is an array, but dimensions were not informed");
			} else if (var.isArray() && var.getDimensionQuantity() != qttIndex) {
				createSemanthicError("On line: " + tk.getLine()  + ". The variable " + tk.getLexem() + " has " + var.getDimensionQuantity() + " dimensions and you informed " + qttIndex);
			} else if (var.getType() == BOOLEAN) {
				createSemanthicError("On line: " + tk.getLine() + ". The variable " + tk.getLexem() + " type is Boolean. Can't read a boolean");
			} else if (!var.isArray() && qttIndex > 0) {
				createSemanthicError("On line: " + tk.getLine() + ". The variable " + tk.getLexem() + " is not an array");
			} else {

			}
		}
		
		index = work.getChildren().indexOf(new Node(SyntaxUtil.LEITURA_I));
		if (index != -1) {
			read(work.getChildren().get(index));
		}
	}

	private void write(Node work) {
		int index = work.getChildren().indexOf(new Node(SyntaxUtil.ESCREVIVEL));
		Pair<Integer, Token> type = ValueSemanthicAnalyzer.valueOfExpSimple(work.getChildren().get(index), function.getFunctionScope());
		if (type.f < 0) {
			createProperError(function, type);
		} else {
		}
		
		index = work.getChildren().indexOf(new Node(SyntaxUtil.ESCREVIVEL_I));
		if (index != -1)
			write(work.getChildren().get(index));
	}

	private void whiled(Node work) {
		Pair<Integer, Token> type = ValueSemanthicAnalyzer.valueOfExpBool(work.getChildren().get(2), function.getFunctionScope());
		if (type.f >= 0) {
			if (type.f != BOOLEAN) {
				createSemanthicError("On line: " + type.s.getLine() + ". Incompatible types, the type of expression is not a Boolean, it is: " + getTypeLiteral(type.f));
			}
		} else {
			createProperError(function, type);
		}
		body(work.getChildren().get(6));
	}

	private void scope(Node work) {
		body(work.getChildren().get(1));
	}

	private void var(Node work) {
		VariablesSemanthicAnalyzer vsa = new VariablesSemanthicAnalyzer(semanthic, work.getChildren().get(2), function.getFunctionScope());
		vsa.analyze();
	}
	
	private void ifed(Node work) {
		Pair<Integer, Token> type = ValueSemanthicAnalyzer.valueOfExpBool(work.getChildren().get(2), function.getFunctionScope());
		if (type.f >= 0) {
			if (type.f != BOOLEAN) {
				createSemanthicError("On line: " + type.s.getLine() + ". Incompatible types, the type of expression is not a Boolean, it is: " + getTypeLiteral(type.f));
			}
		} else {
			createProperError(function, type);
		}
		body(work.getChildren().get(6));
		
		int index = work.getChildren().indexOf(new Node(SyntaxUtil.ELSE_OPC));
		if (index != -1) {
			work = work.getChildren().get(index);
			if (!work.getChildren().get(2).isTerminal())
				body(work.getChildren().get(2));
		}
	}
	
	public static FunctionSymbol funcCall(Node work, SymbolTable functionScope) {
		Token id = work.getChildren().get(0).getToken();
		work = work.getChildren().get(1);
		
		Symbol symbol = SemanthicAnalyzer.get().lookup(id.getLexem(), "global-0");
		if (symbol != null) {
			if (symbol instanceof FunctionSymbol) {
				FunctionSymbol func = (FunctionSymbol)symbol;
				List<Symbol> paramsF = func.getParams();
				
				int qtdO = -1;
				int qtdF = paramsF.size();
				List<Pair<Integer, Token>> paramsO = new ArrayList<>();
				Node prm = work.getChildren().get(1);
				if (prm.isTerminal())
					qtdO = 0;
				else {
					paramsO = parametersResolver(prm, functionScope);
					qtdO = paramsO.size();
				}
				
				if (qtdO == qtdF) {
					if (qtdO == 0) {
						System.out.println("ok");
					} else {
						for (int i = 0; i < paramsF.size(); i++) {
							if (paramsF.get(i).getType() != paramsO.get(i).f.intValue()) {
								if (paramsO.get(i).f >= 0){
									createSemanthicError("On line: "+ id.getLine() + ". For the function " + id.getLexem() + ", the parameter " + (i+1) + " is a " + getTypeLiteral(paramsO.get(i).f.intValue()) + " but function requires " + getTypeLiteral(paramsF.get(i).getType()));
								}else if (paramsO.get(i).f == ERROR_EXP_ARRAY_AS_COMMON) {
									if (paramsF.get(i).getType() == paramsO.get(i).v.getType()) {
										VariableSymbol vf = (VariableSymbol)paramsF.get(i);
										VariableSymbol vo = paramsO.get(i).v;
										if (vf.getDimensionQuantity() != vo.getDimensionQuantity()) {
											createSemanthicError("On line: " + id.getLexem() + ". For the function " + id.getLexem() + ", the parameter " + (i+1) + " requires an arry with " + vf.getDimensionQuantity() + " dimensions");
										} else {
											
										}
									} else {
										createSemanthicError("On line: "+ id.getLine() + ". For the function " + id.getLexem() + ", the array has a different type of function param");
									}
								}
								else{
									createProperError(symbol, paramsO.get(i));
								}
							} else {
								VariableSymbol var = (VariableSymbol)paramsF.get(i);
								if (var.isArray()) {
									createSemanthicError("On line: " + id.getLine() + ". Function requires an array, but you sent a common variable");
								}
							}
						}
					}
				} else {
					createSemanthicError("On line: " + id.getLine() + ". The function " + id.getLexem() + " is defined with " + qtdF + " params, but you sent " + qtdO);
					for (Pair<Integer, Token> in : paramsO) {
						if (in.f <= 0) {
							createProperError(symbol, in);
						}
					}
				}
				return func;
			} else {
				createSemanthicError("On line: " + id.getLine() + " the identifier " + id.getLexem() + " is not a function");
			}
		} else {
			createSemanthicError("On line: " + id.getLine() + " the identifier " + id.getLexem() + " was not declared");
		}
		return null;
	}

	private void funcReturn(Node returnMark) {
		if (returnMark == null) return;
		
		Pair<Integer, Token> type = ValueSemanthicAnalyzer.valueOfExpBool(returnMark.getChildren().get(2), function.getFunctionScope());
		if (type.f >= 0) {
			if (function.getType() != type.f) {
				if (!(function.getType() == FLOAT && type.f == INTEGER))
					createSemanthicError("On line: " + type.s.getLine() + ". Incompatible Types, function " + function.getIdentifier() + " type is " + getTypeLiteral(function.getType()) + " but it's returning " + getTypeLiteral(type.f));
			} else {
				
			}
		} else {
			createProperError(function, type);
		}
	}
	

}
