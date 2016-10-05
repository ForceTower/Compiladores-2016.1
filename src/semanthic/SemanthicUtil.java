package semanthic;

import java.util.ArrayList;
import java.util.List;

import model.Pair;
import model.Token;
import symbols.Symbol;
import symbols.SymbolTable;
import syntax_tree.Node;
import syntax_util.SyntaxUtil;

public class SemanthicUtil {
	public static int 	INTEGER = 0,
						FLOAT = 1,
						CHAR = 2,
						STRING = 3,
						BOOLEAN = 4,
						VOID = 5,
						ANY_TYPE = 6,
						TYPE_MISMATCH = -10,
						ERROR_EXP_UNDECLARED = -11,
						ERROR_EXP_ARRAY_AS_COMMON = -12,
						ERROR_EXP_DIFFERENT_DIMENSIONS = -13,
						ERROR_UNKNOWN = -14;
	
	public static int getTypeToken(Token token) {
		int type = -1;
		if (token.getId() == SyntaxUtil.getTokenId("inteiro"))
			type = INTEGER;
		else if (token.getId() == SyntaxUtil.getTokenId("real"))
			type = FLOAT;
		else if (token.getId() == SyntaxUtil.getTokenId("caractere"))
			type = CHAR;
		else if (token.getId() == SyntaxUtil.getTokenId("cadeia"))
			type = STRING;
		else if (token.getId() == SyntaxUtil.getTokenId("booleano"))
			type = BOOLEAN;
		
		return type;
	}
	
	public static String getTypeLiteral(int type) {
		if (type == 0)
			return "Integer";
		else if (type == 1)
			return "Float";
		else if (type == 2)
			return "Character";
		else if (type == 3)
			return "String";
		else if (type == 4)
			return "Boolean";
		return "No type assigned";
	}
	
	public static void createSemanthicError(String error) {
		SemanthicError se = new SemanthicError(error);
		System.out.println(se);
		SemanthicAnalyzer.get().getErrorsList().add(se);
	}
	
	public static void createProperError(Symbol symbol, Pair<Integer, Token> type) {
		if (type.f == ERROR_EXP_UNDECLARED)
			createSemanthicError("On line: " + type.s.getLine() + ". Identifier " + type.s.getLexem() + " was not declared");
		else if (type.f == ERROR_EXP_ARRAY_AS_COMMON)
			createSemanthicError("On line: " + type.s.getLine() + ". Identifier " + type.s.getLexem() + " is a array, but are used as non-array");
		else if (type.f == ERROR_EXP_DIFFERENT_DIMENSIONS)
			createSemanthicError("On line: " + type.s.getLine() + ". Identifier " + type.s.getLexem() + " has different dimension than the originally declared");
		else if (type.f == TYPE_MISMATCH)
			createSemanthicError("On line: " + type.s.getLine() + ". Incompatible types on expression");
	}
	
	public static List<Pair<Integer, Token>> parametersResolver(Node node, SymbolTable table) {
		List<Pair<Integer, Token>> ret = new ArrayList<>();
		
		parametersResolver(node, ret, table);
		
		return ret;
	}

	private static void parametersResolver(Node node, List<Pair<Integer, Token>> ret, SymbolTable table) {
		int index = node.getChildren().indexOf(new Node(SyntaxUtil.VALOR));
		
		Pair<Integer, Token> type = ValueSemanthicAnalyzer.valueOfExpBool(node.getChildren().get(index), table);
		ret.add(type);
		
		index = node.getChildren().indexOf(new Node(SyntaxUtil.PASSA_PARAM_I));
		if (index != -1)
			parametersResolver(node.getChildren().get(index), ret, table);
	}

}
