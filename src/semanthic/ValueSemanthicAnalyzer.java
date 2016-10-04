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
					//System.out.println("Semanthic error on line: " + node.getToken().getLine() + ". Variable: " + node.getToken().getLexem() + " was not declared");
					return new Pair<Integer, Token>(ERROR_EXP_UNDECLARED, node.getToken());
				}
				
				if (sym instanceof VariableSymbol) {
					VariableSymbol var = (VariableSymbol)sym;
					if (var.isArray()) {
						//System.out.println("Semanthic error on line: " + sym.getToken().getLine() + ". Variable: " + node.getToken().getLexem() + " is an array and can not be used this way");
						return new Pair<Integer, Token>(ERROR_EXP_ARRAY_AS_COMMON, sym.getToken());
					}
				}
				
				return new Pair<Integer, Token>(sym.getType(), sym.getToken());
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
			return valueOfExpBool(node.getChildren().get(1), table);
		} else if (node.getChildren().size() == 7) {
			Symbol sym = SemanthicAnalyzer.get().lookup(node.getChildren().get(6).getToken().getLexem(), table.getName());
			if (sym == null || !(sym instanceof VariableSymbol)) {
				//System.out.println("Semanthic error on line: " + node.getChildren().get(6).getToken().getLine() + ". Variable: " + node.getChildren().get(6).getToken().getLexem() + " was not declared");
				return new Pair<Integer, Token>(ERROR_EXP_UNDECLARED, node.getChildren().get(6).getToken());
			}
			
			VariableSymbol var = (VariableSymbol)sym;
			if (var.isArray()) {
				if (var.getDimensionQuantity() == getQuantityOf(node.getChildren().get(3), 1)) {
					return new Pair<Integer, Token>(var.getType(), var.getToken());
				} else {
					//System.out.println("Semanthic error on line: " + node.getChildren().get(6).getToken().getLine() + ". " + node.getChildren().get(6).getToken().getLexem() + " has " + var.getDimensionQuantity() + " dimensions, but you informed " + getQuantityOf(node.getChildren().get(3), 1));
					return new Pair<Integer, Token>(ERROR_EXP_DIFFERENT_DIMENSIONS, node.getChildren().get(6).getToken());
				}
			}
		}
		
		return new Pair<Integer, Token>(ERROR_UNKNOWN, new Token(9999, "Unknown", 99999, 99999));
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
		if (val.getChildren().size() == 1)
			return typeOfConj(val.getChildren().get(0), table);
		
		Pair<Integer, Token> l = expectsEquals(BOOLEAN, typeOfConj(val.getChildren().get(0), table));
		Pair<Integer, Token> p = expectsEquals(BOOLEAN, typeOfConjOne(val.getChildren().get(1), table));
		return expectsEquals(l, p);
	}
	
	public static Pair<Integer, Token> typeOfConj(Node conj, SymbolTable table) {
		if (conj.getChildren().size() == 1)
			return typeOfRel(conj.getChildren().get(0), table);
		
		Pair<Integer, Token> l = expectsEquals(BOOLEAN, typeOfRel(conj.getChildren().get(0), table));
		Pair<Integer, Token> p = expectsEquals(BOOLEAN, typeOfRelOne(conj.getChildren().get(1), table));
		return expectsEquals(l, p);
	}
	
	public static Pair<Integer, Token> typeOfConjOne(Node conj, SymbolTable table) {
		if (conj.getChildren().size() == 3) {
			Pair<Integer, Token> l = expectsEquals(BOOLEAN, typeOfConj(conj.getChildren().get(1), table));
			Pair<Integer, Token> p = expectsEquals(BOOLEAN, typeOfConjOne(conj.getChildren().get(2), table));
			return expectsEquals(l, p);
		}
		return typeOfConj(conj.getChildren().get(1), table);
	}
	
	public static Pair<Integer, Token> typeOfRel(Node rel, SymbolTable table) {
		if (rel.getChildren().size() == 1)
			return typeOfTerm(rel.getChildren().get(0), table);
		
		if (rel.getChildren().size() == 3) {
			Pair<Integer, Token> l = typeOfTerm(rel.getChildren().get(1), table);
			l = expectsEquals(BOOLEAN, l);
			Pair<Integer, Token> p = typeOfOpRel(rel.getChildren().get(2), table);
			p = expectsEquals(BOOLEAN, p);
			return expectsEquals(l, p);
		}
		
		if (rel.getChildren().get(0).getType() == SyntaxUtil.NOT_OPC)
			return expectsEquals(BOOLEAN, typeOfTerm(rel.getChildren().get(1), table));
		
		Pair<Integer, Token> l = typeOfTerm(rel.getChildren().get(0), table);
		Pair<Integer, Token> p = typeOfOpRel(rel.getChildren().get(1), table);
		
		Pair<Integer, Token> k = resultOf(p, l);
		if (k.f >= 0)
			return new Pair<Integer, Token>(BOOLEAN, k.s);
		else
			return k;
	}
	
	
	public static Pair<Integer, Token> typeOfRelOne(Node rel, SymbolTable table) {
		if (rel.getChildren().size() == 3) {
			Pair<Integer, Token> l = expectsEquals(BOOLEAN, typeOfRel(rel.getChildren().get(1), table));
			Pair<Integer, Token> p = expectsEquals(BOOLEAN, typeOfRelOne(rel.getChildren().get(2), table));
			return expectsEquals(l, p);
		}
		
		return typeOfOpRel(rel.getChildren().get(1), table);
	}
	
	public static Pair<Integer, Token> typeOfOpRel(Node op, SymbolTable table) {
		if (op.getChildren().size() == 3) {
			Pair<Integer, Token> l = typeOfTerm(op.getChildren().get(2), table);
			l = expectsEquals(BOOLEAN, l);
			return l;
		}
		
		return typeOfTerm(op.getChildren().get(1), table);
	}

	public static Pair<Integer, Token> resultOfFields(int type, Pair<Integer, Token> pair, Pair<Integer, Token> pair2) {
		Pair<Integer, Token> t = resultOf(pair, pair2);
		return resultOf(new Pair<Integer, Token>(type, new Token()), t);
	}
	
	public static Pair<Integer, Token> resultOf(Pair<Integer, Token> pair, Pair<Integer, Token> pair2) {
		if (pair.f == ANY_TYPE)
			return pair2;
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

	private static int getQuantityOf(Node node, int i) {
		return 0;
	}

}
