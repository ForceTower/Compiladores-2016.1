package syntax_util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import debug.Debug;
import model.Token;
import model.TokenFactory;

public class SyntaxUtil {
	public int 	START = 200,
				INICIO_CONST_K_FUNC = 201,
				DECL_CONST_VAR_DERIVA = 202,
				INICIO_CONST_FUNC = 203, //Unused
				INICIO_VAR_FUNC = 204, //Unused
				INICIO_FUNC = 205, //Unused
				//PROGRAM = 206, //Unused
				DECL_CONST = 207,
				DECL_VAR = 208,
				DECL_FUNC = 209,
				DECL_MAIN = 210,
				DECL_CONST_CONTINUO = 211,
				_CONST_VAR_FUNC = 212, //Unused
				DECL_CONST_I = 213,
				DECL_CONST_II = 214,
				VALOR = 215,
				EXP_RELACIONAL_BOOLEANA = 216, //Unused
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
				DECL_FUNC_I = 250,
				DECL_FUNC_I_R = 251,
				DECL_FUNC_I_V = 252,
				CORPO = 253,
				PARAMETROS = 254,
				RETORNO_FUNC = 255,
				ARRAY_PARAM = 256,
				PARAMETROS_I = 257,
				ARRAY_INDEXES_OPT = 258,
				ARRAY_PARAM_I = 259,
				CMD_LEIA = 260,
				CMD_ESCREVA = 261,
				CMD_SE = 262,
				CMD_ENQUANTO = 263,
				CMD_VAR = 264,
				CMD_ESCOPO = 265,
				CMD_ATTRIB_CHAMA_IDEN = 266,
				CMD_ATTRIB_CHAMA_MATRIZ = 267,
				LEITURA_I = 268,
				ESCREVIVEL_I = 269,
				ELSE_OPC = 270,
				WHOS_NEXT = 271,
				WHOS_NEXT_ATTRIB = 272,
				WHOS_NEXT_FUNC = 273,
				ESCREVIVEL = 274,
				TERMO_E = 278,
				TERMO_I_E = 279,
				FATOR_E = 280,
				FATOR_I_E = 281,
				PAR_ESC_PAR = 282,
				COMANDOS = 283,
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
				END = 400,
				FATHER_RETURN = 401;
	
	public static int getTokenId(String lexeme) {
		Token t = TokenFactory.findToken(lexeme);
		return t != null ? t.getId() : -1;
	}
	
	public static int getTokenType(String param) {
		if (param.equalsIgnoreCase("primitive_types"))
			return 1;
		
		return -1;
		
	}
	
	private Hashtable<Integer, ArrayList<Integer>> follows;
	
	public SyntaxUtil() throws IOException {
		follows = new Hashtable<>();
		initFollows();
	}
	
	public void initFollows() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader("follows.txt"));
		
		String line = null;
		while ((line = reader.readLine()) != null) {
			ArrayList<Integer> list = new ArrayList<>();
			
			String[] lineSplit = line.split(" # ");
			int state = Integer.parseInt(lineSplit[0]);
			
			for (int i = 1; i < lineSplit.length; i++) {
				String follow = lineSplit[i];
				Integer integer = null;
				
				if (follow.equals("Num"))
					integer = TokenFactory.NUM_CONST;
				else if (follow.equals("Caractere"))
					integer = TokenFactory.CHAR_CONST;
				else if (follow.equals("Cadeia"))
					integer = TokenFactory.STRING_CONST;
				else if (follow.equals("Identificador"))
					integer = TokenFactory.IDENT;
				else if (follow.equals("$"))
					integer = END;
				else
					integer = getTokenId(follow.trim());
				
				if (integer == -1) {
					System.err.println(follow);
					System.err.println("Invalid File");
				}
				
				list.add(integer);
			}
			
			follows.put(state, list);
		}
		
		reader.close();
	}

	public List<Integer> getFollowsOfState(int state) {
		Debug.println("Follow State: " + state);
		ArrayList<Integer> temp = follows.get(state);
		if (temp != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("Follows: ");
			boolean n = false;
			for (Integer e : temp) {
				if (n)
					sb.append(", ");
				sb.append(TokenFactory.meaning_messages.get(e));	
				n = true;
			}
			Debug.println(sb.toString());
			return temp;
		} else {
			Debug.println("Has no follow...");
			return new ArrayList<Integer>();
		}
	}
	

}
