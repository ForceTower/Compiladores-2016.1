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
				VALOR = 215,
				EXP_RELACIONAL_BOOLEANA = 216,
				EXPRESSAO_CONJUNTA = 217,
				EXPRESSAO_CONJUNTA_I = 218,
				EXPRESSAO_RELACIONAL = 219,
				EXPRESSAO_RELACIONAL_I = 220,
				NOT_OPC = 221,
				EXPR_SIMPLES = 222,
				OPERAR_RELACIONALMENTE = 223,
				OPERADOR_MAIS_MENOS = 224,
				PLUS_CONSUME = 225,
				MINUS_CONSUME = 226,
				TERMO = 227,
				TERMO_I = 228,
				FATOR = 229,
				FATOR_I = 230,
				IDENTIFICADOR_FUNCAO = 231,
				ARRAY_IDENTIFICADOR = 232,
				PAR_VALOR_PAR = 233,
				POSSIBLE_FUNC = 234,
				ARRAY_INDEXES = 235,
				ARRAY = 236,
				ARRAY_I = 237,
				PASSA_PARAM = 238,
				PASSA_PARAM_I = 239,
				FATOR_I_MD = 240,
				OPERAR_RELACIONALMENTE_CONSUME = 241,
				DECL_VAR_CONTINUO = 242,
				DECL_VAR_I = 243,
				DECL_VAR_II = 244,
				CONSUME_DIF = 376,
				CONSUME_GT = 377,
				CONSUME_GE = 378,
				CONSUME_LT = 379,
				CONSUME_LE = 380,
				CONSUME_MULT = 381,
				CONSUME_DIV = 382,
				CONSUME_TRUE = 383,
				CONSUME_FALSE = 384,
				CONSUME_NUM_CONST = 385,
				CONSUME_CHAR_CONST = 386,
				CONSUME_ID_CONST = 387,
				CONSUME_STRING_CONST = 388,
				NUM_CONST_CONSUME = 389,
				STRING_CONST_CONSUME = 390,
				CHAR_CONST_CONSUME = 391,
				INTEGER_CONSUME = 393,
				FLOAT_CONSUME = 394,
				CHAR_CONSUME = 395,
				STRING_CONSUME = 396,
				BOOL_CONSUME = 397,
				TYPE = 398,
				EPSILON = 399,
				END = 400;
	
	public static int getTokenId(String lexeme) {
		Token t = TokenFactory.findToken(lexeme);
		return t != null ? t.getId() : -1;
	}
	
	public static int getTokenType(String param) {
		if (param.equalsIgnoreCase("primitive_types"))
			return 1;
		
		return -1;
		
	}
	

}
