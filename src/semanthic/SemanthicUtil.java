package semanthic;

import model.Token;
import syntax_util.SyntaxUtil;

public class SemanthicUtil {
	public static int 	INTEGER = 0,
						FLOAT = 1,
						CHAR = 2,
						STRING = 3,
						BOOLEAN = 4,
						VOID = 5;
	
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
		System.out.println(error);
		SemanthicAnalyzer.get().getErrorsList().add(error);
	}

}
