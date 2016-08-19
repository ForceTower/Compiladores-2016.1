package syntactic_util;

import model.Token;
import model.TokenFactory;

public class SyntacticUtil {
	public int 	START = 200,
				INICIO_CONST_K_FUNC = 201,
				DECL_CONST_VAR_DERIVA = 202,
				INICIO_CONST_FUNC = 203,
				INICIO_VAR_FUNC = 204,
				INICIO_FUNC = 205,
				PROGRAM = 206,
				DECL_CONST = 207,
				DECL_VAR = 208,
				DECL_FUNC = 209,
				DECL_MAIN = 210,
				DECL_CONST_CONTINUO = 211,
				_CONST_VAR_FUNC = 212,
				DECL_CONST_I = 213,
				DECL_CONST_II = 214,
				EPSILON = 399,
				END = 400;
	
	public static int getTokenId(String lexeme) {
		Token t = TokenFactory.findToken(lexeme);
		return t != null ? t.getId() : -1;
	}
	

}
