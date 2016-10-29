package semanthic;

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

public class ValueSemanthicAnalyzer extends SemanthicUtil{

	public static Pair<Integer, Token> valueOfExpSimple(Node exp_simple, SymbolTable table) {
		return typeOfTerm(exp_simple, table);
	}
	
	public static Pair<Integer, Token> valueOfExpBool(Node exp_bool, SymbolTable table) {
		return typeOfValue(exp_bool, table);
	}
	
	public static Pair<Integer, Token> factorization(Node node, SymbolTable table) {
		if (node.getChildren().size() == 1) {
			node = node.getChildren().get(0);
			int id = node.getToken().getId();
			if (id == TokenFactory.IDENT) {
				Symbol sym = SemanthicAnalyzer.get().lookup(node.getToken().getLexem(), table.getName());
				if (sym == null || sym instanceof FunctionSymbol) {
					return new Pair<Integer, Token>(ERROR_EXP_UNDECLARED, node.getToken());
				}
				
				if (sym instanceof VariableSymbol) {
					VariableSymbol var = (VariableSymbol)sym;
					if (var.isArray()) {
						Pair<Integer, Token> pp = new Pair<Integer, Token>(ERROR_EXP_ARRAY_AS_COMMON, node.getToken());
						pp.v = var;
						return pp;
					}
				}
				
				return new Pair<Integer, Token>(sym.getType(), node.getToken());
			} 
			else if (id == TokenFactory.CHAR_CONST)
				return new Pair<Integer, Token>(CHAR, node.getToken());
			else if (id == TokenFactory.STRING_CONST)
				return new Pair<Integer, Token>(STRING, node.getToken());
			else if (id == TokenFactory.NUM_CONST) {
				if (node.getToken().getLexem().contains("."))
					return new Pair<Integer, Token>(FLOAT, node.getToken());
				else
					return new Pair<Integer, Token>(INTEGER, node.getToken());
			}
			else if (id == SyntaxUtil.getTokenId("verdadeiro") || id == SyntaxUtil.getTokenId("falso"))
				return new Pair<Integer, Token>(BOOLEAN, node.getToken());;
			
		} else if (node.getChildren().size() == 3) {
			if (node.getChildren().get(0).getToken().getId() == TokenFactory.IDENT) {
				return functionCallInExp(node, table);
			} else {
				return valueOfExpBool(node.getChildren().get(1), table);
			}
		} else if (node.getChildren().size() == 7 || node.getChildren().size() == 6) {
			if (node.getChildren().get(0).getToken().getId() == TokenFactory.IDENT) {
				return functionCallInExp(node, table);
			}
			
			int max = node.getChildren().size() == 7 ? 6 : 5;
			
			Symbol sym = SemanthicAnalyzer.get().lookup(node.getChildren().get(max).getToken().getLexem(), table.getName());
			if (sym == null || !(sym instanceof VariableSymbol)) {
				return new Pair<Integer, Token>(ERROR_EXP_UNDECLARED, node.getChildren().get(max).getToken());
			}
			
			VariableSymbol var = (VariableSymbol)sym;
			if (var.isArray()) {
				checkIndexes(node, table, var);
				if (var.getDimensionQuantity() == getQuantityOf(node.getChildren().get(3), 1)) {
					return new Pair<Integer, Token>(var.getType(), node.getChildren().get(max).getToken());
				} else {
					return new Pair<Integer, Token>(ERROR_EXP_DIFFERENT_DIMENSIONS, node.getChildren().get(max).getToken());
				}
			} else {
				return new Pair<Integer, Token>(ERROR_EXP_ACCESSING_COMMON_AS_ARRAY, node.getChildren().get(max).getToken());
			}
		} else {
			if (node.getChildren().get(0).getToken().getId() == TokenFactory.IDENT) {
				return functionCallInExp(node, table);
			}
		}
		
		return new Pair<Integer, Token>(ERROR_UNKNOWN, new Token(9999, "Not a thing", 99999, 99999));
	}
	
	private static Pair<Integer, Token> functionCallInExp(Node node, SymbolTable table) {
		Token tk = node.getChildren().get(0).getToken();
		FunctionSymbol sym = BodySemanthicAnalyzer.funcCall(node, table);
		if (sym == null) {
			return new Pair<Integer, Token> (ERROR_EXP_UNDECLARED, tk);
		}
		return new Pair<Integer, Token>(sym.getType(), tk);
	}

	public static Pair<Integer, Token> typeOfTerm(Node exp, SymbolTable table) {
		if (exp.getType() == SyntaxUtil.FATOR || exp.getType() == SyntaxUtil.FATOR_E)
			return factorization(exp, table);
		
		if (exp.getChildren().size() == 1)
			return typeOfTerm(exp.getChildren().get(0), table);
		
		if (exp.getChildren().size() == 3) 
			return resultOfFields(INTEGER, typeOfTerm(exp.getChildren().get(1), table), typeOfTerm(exp.getChildren().get(2), table));
		
		List<Node> l = exp.getChildren();
		
		if (l.get(0).getType() == SyntaxUtil.OPERADOR_MAIS_MENOS || l.get(0).getType() == SyntaxUtil.FATOR_I_MD)
			return resultOf(new Pair<Integer, Token>(INTEGER, new Token()), typeOfTerm(l.get(1), table));
		else
			return resultOfFields(INTEGER, typeOfTerm(exp.getChildren().get(0), table), typeOfTerm(exp.getChildren().get(1), table));
	}
	
	public static Pair<Integer, Token> typeOfValue(Node val, SymbolTable table) {
		if (val.getChildren().size() == 1) //<Expr_Conjunta>
			return typeOfConj(val.getChildren().get(0), table);
		
		//<Expr_Conjunta> <Expr_Conjunta_I>
		Pair<Integer, Token> l = expectsEquals(BOOLEAN, typeOfConj(val.getChildren().get(0), table));
		Pair<Integer, Token> p = expectsEquals(BOOLEAN, typeOfConjOne(val.getChildren().get(1), table));
		return expectsEquals(l, p);
	}
	
	public static Pair<Integer, Token> typeOfConj(Node conj, SymbolTable table) {
		if (conj.getChildren().size() == 1) //<Expr_Relacional>
			return typeOfRel(conj.getChildren().get(0), table);
		
		//<Expr_Relacional> <Expr_Relacional_I> 
		Pair<Integer, Token> l = expectsEquals(BOOLEAN, typeOfRel(conj.getChildren().get(0), table));
		Pair<Integer, Token> p = expectsEquals(BOOLEAN, typeOfRelOne(conj.getChildren().get(1), table));
		return expectsEquals(l, p);
	}
	
	public static Pair<Integer, Token> typeOfConjOne(Node conj, SymbolTable table) {
		if (conj.getChildren().size() == 3) { //'ou' <Expr_Conjunta> <Expr_Conjunta_I>
			Pair<Integer, Token> l = expectsEquals(BOOLEAN, typeOfConj(conj.getChildren().get(1), table));
			Pair<Integer, Token> p = expectsEquals(BOOLEAN, typeOfConjOne(conj.getChildren().get(2), table));
			return expectsEquals(l, p);
		}
		return typeOfConj(conj.getChildren().get(1), table); // 'ou' <Expr_Conjunta>
	}
	
	public static Pair<Integer, Token> typeOfRel(Node rel, SymbolTable table) {
		if (rel.getChildren().size() == 1) //<Expr_Simples>
			return typeOfTerm(rel.getChildren().get(0), table);
		
		if (rel.getChildren().size() == 3) { //<Not_Opc> <Expr_Simples> <Operar_Relacionalmente>
			Pair<Integer, Token> l = typeOfTerm(rel.getChildren().get(1), table);
			l = expectsEquals(BOOLEAN, l);
			Pair<Integer, Token> p = typeOfOpRel(rel.getChildren().get(2), table);
			p = expectsEquals(BOOLEAN, p);
			return expectsEquals(l, p);
		}
		
		if (rel.getChildren().get(0).getType() == SyntaxUtil.NOT_OPC) // <Not_Opc> <Expr_Simples>
			return expectsEquals(BOOLEAN, typeOfTerm(rel.getChildren().get(1), table));
		
		//<Expr_Simples> <Operar_Relacionalmente>
		Pair<Integer, Token> l = typeOfTerm(rel.getChildren().get(0), table);
		Pair<Integer, Token> p = typeOfOpRel(rel.getChildren().get(1), table);
		
		Pair<Integer, Token> k = resultOf(p, l);
		if (k.f >= 0)
			return new Pair<Integer, Token>(BOOLEAN, k.s);
		else
			return k;
	}
	
	
	public static Pair<Integer, Token> typeOfRelOne(Node rel, SymbolTable table) {
		if (rel.getChildren().size() == 3) { //'e' <Expr_Relacional> <Expr_Relacional_I>
			Pair<Integer, Token> l = expectsEquals(BOOLEAN, typeOfRel(rel.getChildren().get(1), table));
			Pair<Integer, Token> p = expectsEquals(BOOLEAN, typeOfRelOne(rel.getChildren().get(2), table));
			return expectsEquals(l, p);
		}
		
		//'e' <Expr_Relacional>
		return typeOfRel(rel.getChildren().get(1), table);
	}
	
	public static Pair<Integer, Token> typeOfOpRel(Node op, SymbolTable table) {
		if (op.getChildren().size() == 3) { //<Operador_Relacional> <Not_Opc> <Expr_Simples>
			Pair<Integer, Token> l = typeOfTerm(op.getChildren().get(2), table);
			l = expectsEquals(BOOLEAN, l);
			return l;
		}
		
		if (op.getChildren().size() == 2) { //<Operador_Relacional> <Expr_Simples>
			return typeOfTerm(op.getChildren().get(1), table); 
		}
		
		//this is a bug
		System.out.println("Possivel dando ruim aqui");
		return typeOfTerm(op.getChildren().get(0), table);
	}

	public static Pair<Integer, Token> resultOfFields(int type, Pair<Integer, Token> pair, Pair<Integer, Token> pair2) {
		Pair<Integer, Token> t = resultOf(pair, pair2);
		return resultOf(new Pair<Integer, Token>(type, new Token()), t);
	}
	
	public static Pair<Integer, Token> resultOf(Pair<Integer, Token> pair, Pair<Integer, Token> pair2) {
		if (pair.f == ANY_TYPE)
			return pair2;
		if (pair.f < 0 && pair2.f < 0){
			createProperError(null, pair);
			return new Pair<Integer, Token>(pair2.f, pair2.s);
		}
		if (pair.f < 0)
			return pair;
		if (pair2.f < 0)
			return pair2;
		
		if (pair.f == INTEGER && pair2.f == FLOAT)
			return new Pair<Integer, Token>(FLOAT, pair2.s);
		else if (pair.f == FLOAT && pair2.f == INTEGER)
			return new Pair<Integer, Token>(FLOAT, pair2.s);
		else {
			if (pair.f == pair2.f)
				return pair2;
			else
				return new Pair<Integer, Token>(TYPE_MISMATCH, pair2.s);
		}
	}
	
	public static Pair<Integer, Token> expectsEquals(Integer l, Pair<Integer, Token> p) {
		return expectsEquals(new Pair<Integer, Token>(l, new Token()), p);
	}
	
	public static Pair<Integer, Token> expectsEquals(Pair<Integer, Token> one, Pair<Integer, Token> two) {
		if (one.f < 0)
			return one;
		if (two.f < 0)
			return two;
		if (one.f == two.f)
			return two;
		
		return new Pair<Integer, Token>(TYPE_MISMATCH, two.s);
	}

	public static int getQuantityOf(Node array_i, int i) {
		if (array_i.isTerminal())
			return i;
		else {
			List<Node> list = array_i.getChildren();
			if (list.size() == 3)
				return getQuantityOf(list.get(2), i+1);
			return i+1;
		}
	}
	
	public static void checkIndexes(Node node, SymbolTable table, VariableSymbol symbol) {
		int p = node.getChildren().indexOf(new Node(SyntaxUtil.EXPR_SIMPLES));
		Node index = node.getChildren().get(p);
		Pair<Integer, Token> type = ValueSemanthicAnalyzer.valueOfExpSimple(index, table);
		if (type.f != INTEGER) {
			if (type.f > 0)
				createSemanthicError(type.s.getLine(), "Na linha: " + type.s.getLine() + ". Uma expressão na dimensão do vetor " + symbol.getIdentifier() + " é um " + getTypeLiteral(type.f) + ", mas deve ser um Inteiro para se tornar uma dimensão");
			else
				createProperError(symbol, type);
		}
		
		if (node.getChildren().contains(new Node(SyntaxUtil.ARRAY_I))) {
			Node n = node.getChildren().get(node.getChildren().indexOf(new Node(SyntaxUtil.ARRAY_I)));
			checkIndexes(n, table, symbol);
		}
	}

}
