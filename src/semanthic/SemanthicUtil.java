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
						ERROR_UNKNOWN = -14,
						ERROR_EXP_ACCESSING_COMMON_AS_ARRAY = -15;
	
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
			return "Inteiro";
		else if (type == 1)
			return "Real";
		else if (type == 2)
			return "Caractere";
		else if (type == 3)
			return "Cadeia";
		else if (type == 4)
			return "Booleano";
		else if (type == 5)
			return "Vazio";
		return "Nenhum tipo anexado";
	}
	
	public static void createSemanthicError(int line, String error) {
		SemanthicError se = new SemanthicError(line, error);
		System.out.println(se);
		SemanthicAnalyzer.get().getErrorsList().add(se);
	}
	
	public static void createProperError(Symbol symbol, Pair<Integer, Token> type) {
		if (type.f == ERROR_EXP_UNDECLARED)
			createSemanthicError(type.s.getLine(), "Na linha: " + type.s.getLine() + ". Identificador " + type.s.getLexem() + " não foi declarado");
		else if (type.f == ERROR_EXP_ARRAY_AS_COMMON)
			createSemanthicError(type.s.getLine(), "Na linha: " + type.s.getLine() + ". Identificador " + type.s.getLexem() + " é um vetor mas é usado como um não-vetor");
		else if (type.f == ERROR_EXP_DIFFERENT_DIMENSIONS)
			createSemanthicError(type.s.getLine(), "Na linha: " + type.s.getLine() + ". Identificador " + type.s.getLexem() + " tem dimensões diferentes do que foi originalmente declarado");
		else if (type.f == TYPE_MISMATCH)
			createSemanthicError(type.s.getLine(), "Na linha: " + type.s.getLine() + ". Tipos incompativeis na expressão");
		else if (type.f == ERROR_EXP_ACCESSING_COMMON_AS_ARRAY)
			createSemanthicError(type.s.getLine(), "Na linha: " + type.s.getLine() + ". Variavel " + type.s.getLexem() + " não é um vetor");
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
