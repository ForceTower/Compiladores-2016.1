package semanthic;

import java.util.ArrayList;
import java.util.List;

import model.Pair;
import model.Token;
import model.TokenFactory;
import symbols.FunctionSymbol;
import symbols.Symbol;
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
			funcCall(work);
		}
	}

	private void attribuition(Node work) {
		System.out.println("attr");
		boolean allright = false;
		Token id = work.getChildren().get(0).getToken();
		work = work.getChildren().get(1);
		Pair<Integer, Token> type = ValueSemanthicAnalyzer.valueOfExpBool(work.getChildren().get(1), function.getFunctionScope());
		
		Symbol symbol = semanthic.lookup(id.getLexem(), function.getFunctionScope().getName());
		if (symbol != null) {
			if (!(symbol instanceof VariableSymbol)) {
				createSemanthicError("On line: " + id.getLine() + ". The value of identifier " + id.getLexem() + " can not be changed");
			} else {
				allright = true;
				System.out.println("ok");
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
		System.out.println("read");
	}

	private void write(Node work) {
		System.out.println("write");
		int index = work.getChildren().indexOf(new Node(SyntaxUtil.ESCREVIVEL));
		Pair<Integer, Token> type = ValueSemanthicAnalyzer.valueOfExpSimple(work.getChildren().get(index), function.getFunctionScope());
		if (type.f < 0) {
			createProperError(function, type);
		} else {
			System.out.println("ok");
		}
		
		index = work.getChildren().indexOf(new Node(SyntaxUtil.ESCREVIVEL_I));
		if (index != -1)
			write(work.getChildren().get(index));
	}

	private void whiled(Node work) {
		System.out.println("while");
		Pair<Integer, Token> type = ValueSemanthicAnalyzer.valueOfExpBool(work.getChildren().get(2), function.getFunctionScope());
		if (type.f >= 0) {
			if (type.f != BOOLEAN) {
				createSemanthicError("On line: " + type.s.getLine() + ". Incompatible types, the type of expression is not a Boolean, it is: " + getTypeLiteral(type.f));
			}
		} else {
			createProperError(function, type);
		}
		body(work.getChildren().get(6));
		System.out.println("ok");
	}

	private void scope(Node work) {
		System.out.println("scope");
		body(work.getChildren().get(1));
		System.out.println("ok");
	}

	private void var(Node work) {
		System.out.println("var");
		VariablesSemanthicAnalyzer vsa = new VariablesSemanthicAnalyzer(semanthic, work.getChildren().get(2), function.getFunctionScope());
		vsa.analyze();
		System.out.println("ok");
	}
	
	private void ifed(Node work) {
		System.out.println("if");
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
			System.out.println("else");
			work = work.getChildren().get(index);
			if (!work.getChildren().get(2).isTerminal())
				body(work.getChildren().get(2));
		}
	}
	
	private void funcCall(Node work) {
		System.out.println("call");
		Token id = work.getChildren().get(0).getToken();
		work = work.getChildren().get(1);
		
		Symbol symbol = semanthic.lookup(id.getLexem(), "global-0");
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
					paramsO = parametersResolver(prm, function.getFunctionScope());
					qtdO = paramsO.size();
				}
				
				if (qtdO == qtdF) {
					if (qtdO == 0) {
						System.out.println("ok");
					} else {
						for (int i = 0; i < paramsF.size(); i++) {
							if (paramsF.get(i).getType() != paramsO.get(i).f.intValue()) {
								if (paramsO.get(i).f >= 0)
									createSemanthicError("On line: "+ id.getLine() + ". For the function " + id.getLexem() + ", the parameter " + (i+1) + " is a " + getTypeLiteral(paramsO.get(i).f.intValue()) + " but function requires " + getTypeLiteral(paramsF.get(i).getType()));
								else{
									createProperError(symbol, paramsO.get(i));
								}
									//createSemanthicError("On line: " + id.getLine() + ". Expression of parameter cannot be resolved into a type");
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
				
			} else {
				createSemanthicError("On line: " + id.getLine() + " the identifier " + id.getLexem() + " is not a function");
			}
		} else {
			createSemanthicError("On line: " + id.getLine() + " the identifier " + id.getLexem() + " was not declared");
		}
	}

	private void funcReturn(Node returnMark) {
		if (returnMark == null) return;
		
		Pair<Integer, Token> type = ValueSemanthicAnalyzer.valueOfExpBool(returnMark.getChildren().get(2), function.getFunctionScope());
		if (type.f >= 0) {
			if (function.getType() != type.f) {
				if (!(function.getType() == FLOAT && type.f == INTEGER))
					createSemanthicError("On line: " + type.s.getLine() + ". Incompatible Types, function " + function.getIdentifier() + " type is " + getTypeLiteral(function.getType()) + " but it's returning " + getTypeLiteral(type.f));
			} else {
				System.out.println("ret ok");
			}
		} else {
			createProperError(function, type);
		}
	}
	

}
