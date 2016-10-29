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
				createSemanthicError(id.getLine(), "Na linha: " + id.getLine() + ". O valor do identificador " + id.getLexem() + " não pode ser alterado");
			} else {
				VariableSymbol var = (VariableSymbol)symbol;
				allright = true;
				if (arrayAccess && !var.isArray()) {
					createSemanthicError(id.getLine(), "Na linha: " + id.getLine() + ". A variável " + id.getLexem() + " não é um vetor");
				} else if (arrayAccess && var.isArray()) {
					if (indexQtt != var.getDimensionQuantity()) {
						createSemanthicError(id.getLine(), "Na linha: " + id.getLine() + ". A variável " + id.getLexem() + " tem " + var.getDimensionQuantity() + " dimensões, mas você usou " + indexQtt);
					}
				}
			}
		} else {
			createSemanthicError(id.getLine(), "Na linha: " + id.getLine() + ". A variável " + id.getLexem() + " não foi declarada");
		}
		
		if (allright && type.f >= 0) {
			if (symbol.getType() != type.f) {
				if (!(symbol.getType() == FLOAT && type.f == INTEGER))
					createSemanthicError(type.s.getLine(), "Na linha: " + type.s.getLine() + ". Tipos incompatíveis, identificador é " + getTypeLiteral(symbol.getType()) + " e a expressão retorna um " + getTypeLiteral(type.f));
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
				createSemanthicError(tk.getLine(), "Na linha: " + tk.getLine() + ". O valor de " + tk.getLexem() + " não pode ser alterado, este identificador foi declarado como Função ou Constante");
			}
		} else {
			createSemanthicError(tk.getLine(), "Na linha: " + tk.getLine() + ". A variável " + tk.getLexem() + " não foi declarada");
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
				createSemanthicError(tk.getLine(), "Na linha: " + tk.getLine() + ". A variável " + tk.getLexem() + " é um vetor, mas nenhuma dimensão foi informada");
			} else if (var.isArray() && var.getDimensionQuantity() != qttIndex) {
				createSemanthicError(tk.getLine(), "Na linha: " + tk.getLine()  + ". A variável " + tk.getLexem() + " possui " + var.getDimensionQuantity() + " dimensões, mas você usou " + qttIndex + " dimensões");
			} else if (var.getType() == BOOLEAN) {
				createSemanthicError(tk.getLine(), "Na linha: " + tk.getLine() + ". O tipo da variável " + tk.getLexem() + " é booleana. Não é possivel ler uma booleana");
			} else if (!var.isArray() && qttIndex > 0) {
				createSemanthicError(tk.getLine(), "Na linha: " + tk.getLine() + ". A variável " + tk.getLexem() + " não é um vetor");
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
				createSemanthicError(type.s.getLine(), "Na linha: " + type.s.getLine() + ". Tipos incompatíveis, o tipo da expressão não é um Booleano, ela é: " + getTypeLiteral(type.f));
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
				createSemanthicError(type.s.getLine(), "Na linha: " + type.s.getLine() + ". Tipos incompatíveis, O tipo da expressão não é um Booleano, ela é: " + getTypeLiteral(type.f));
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
						//System.out.println("ok");
					} else {
						for (int i = 0; i < paramsF.size(); i++) {
							if (paramsF.get(i).getType() != paramsO.get(i).f.intValue()) {
								if (paramsO.get(i).f >= 0){
									createSemanthicError(id.getLine(), "Na linha: "+ id.getLine() + ". Na função " + id.getLexem() + ", o " + (i+1) + " parâmetro é um " + getTypeLiteral(paramsO.get(i).f.intValue()) + " mas a função requer " + getTypeLiteral(paramsF.get(i).getType()));
								}else if (paramsO.get(i).f == ERROR_EXP_ARRAY_AS_COMMON) {
									if (paramsF.get(i).getType() == paramsO.get(i).v.getType()) {
										VariableSymbol vf = (VariableSymbol)paramsF.get(i);
										VariableSymbol vo = paramsO.get(i).v;
										if (vf.getDimensionQuantity() != vo.getDimensionQuantity()) {
											createSemanthicError(id.getLine(), "Na linha: " + id.getLine() + ". Na função " + id.getLexem() + ", o " + (i+1) + " parâmetro requer um vetor com " + vf.getDimensionQuantity() + " dimensões");
										} else {
											//Sucesso!!!!!!
										}
									} else {
										createSemanthicError(id.getLine(), "Na linha: "+ id.getLine() + ". Na função " + id.getLexem() + ", o vetor possui dimensões diferentes do que a assinatura da função");
									}
								}
								else{
									createProperError(symbol, paramsO.get(i));
								}
							} else {
								VariableSymbol var = (VariableSymbol)paramsF.get(i);
								if (var.isArray()) {
									createSemanthicError(id.getLine(), "Na linha: " + id.getLine() + ". A função requer um vetor, mas você tentou enviar uma variável comum");
								}
							}
						}
					}
				} else {
					createSemanthicError(id.getLine(), "Na linha: " + id.getLine() + ". A função " + id.getLexem() + " foi definida com " + qtdF + " parâmetros, mas você está usando " + qtdO + " parâmetros");
					for (Pair<Integer, Token> in : paramsO) {
						if (in.f <= 0) {
							createProperError(symbol, in);
						}
					}
				}
				return func;
			} else {
				createSemanthicError(id.getLine(), "Na linha: " + id.getLine() + ". O identificador " + id.getLexem() + " não é uma função");
			}
		} else {
			createSemanthicError(id.getLine(), "Na linha: " + id.getLine() + ". O identificador " + id.getLexem() + " não foi declarado");
		}
		return null;
	}

	private void funcReturn(Node returnMark) {
		if (returnMark == null) return;
		
		Pair<Integer, Token> type = ValueSemanthicAnalyzer.valueOfExpBool(returnMark.getChildren().get(2), function.getFunctionScope());
		if (type.f >= 0) {
			if (function.getType() != type.f) {
				if (!(function.getType() == FLOAT && type.f == INTEGER))
					createSemanthicError(type.s.getLine(), "Na linha: " + type.s.getLine() + ". Tipos incompatíveis, o tipo da função " + function.getIdentifier() + " é " + getTypeLiteral(function.getType()) + " mas está retornando " + getTypeLiteral(type.f));
			} else {
				
			}
		} else {
			createProperError(function, type);
		}
	}
	

}
