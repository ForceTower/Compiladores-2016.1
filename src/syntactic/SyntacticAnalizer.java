package syntactic;

import java.util.List;
import java.util.Stack;

import debug.Debug;
import model.Token;
import model.TokenFactory;
import syntactic_util.SyntacticUtil;

public class SyntacticAnalizer extends SyntacticUtil {
	private List<Token> tokens;
	private Stack<Integer> stack;
	int[][] syntacticTable;
	private int currentToken;
	
	public SyntacticAnalizer(List<Token> tokens) {
		super();
		this.tokens = tokens;
		this.stack = new Stack<>();
		syntacticTable = new int[800][800];
		currentToken = 0;
		
		initializeTable();
		prepareTable();
		prepareStack();
	}

	public void initializeTable() {
		for (int i = 0; i < 800; i++)
			for (int j = 0; j < 800; j++)
				syntacticTable[i][j] = -1;
	}
	
	public void prepareTable() {
		//INICIO GRAMATICA
		syntacticTable[START][getTokenId("const")]		= INICIO_CONST_K_FUNC;
		syntacticTable[START][getTokenId("var")] 		= INICIO_VAR_FUNC;
		syntacticTable[START][getTokenId("funcao")] 	= INICIO_FUNC;
		syntacticTable[START][getTokenId("programa")] 	= INICIO_FUNC;
		
		//INICIO GRAMATICA DECL_CONST
		fillRow(DECL_CONST, DECL_CONST);
		//syntacticTable[DECL_CONST][getTokenId("const")] = DECL_CONST;
		//fillRow(DECL_CONST, EPSILON);
		syntacticTable[DECL_CONST_I][getTokenId(",")] 	= DECL_CONST_I;
		syntacticTable[DECL_CONST_I][getTokenId(";")] 	= EPSILON;
		
		syntacticTable[DECL_CONST_II][getTokenId("inteiro")] 	= DECL_CONST_II;
		syntacticTable[DECL_CONST_II][getTokenId("real")] 	= DECL_CONST_II;
		syntacticTable[DECL_CONST_II][getTokenId("caractere")] 	= DECL_CONST_II;
		syntacticTable[DECL_CONST_II][getTokenId("cadeia")] 	= DECL_CONST_II;
		syntacticTable[DECL_CONST_II][getTokenId("booleano")] 	= DECL_CONST_II;
		syntacticTable[DECL_CONST_II][getTokenId("fim")] 	= EPSILON;
		
		fillRow(DECL_CONST_CONTINUO, DECL_CONST_CONTINUO);
		syntacticTable[DECL_CONST_VAR_DERIVA][getTokenId("var")] 	= _CONST_VAR_FUNC;
		
		
		//INICIO GRAMATICA DECL_VAR
		fillRow(DECL_VAR, DECL_VAR);
		fillRow(DECL_VAR_CONTINUO, DECL_VAR_CONTINUO);
		
		fillRow(DECL_VAR_I, EPSILON);
		syntacticTable[DECL_VAR_I][getTokenId(",")] = DECL_VAR_I;
		fillRow(DECL_VAR_II, EPSILON);
		syntacticTable[DECL_VAR_II][getTokenId("inteiro")] 	= DECL_VAR_II;
		syntacticTable[DECL_VAR_II][getTokenId("real")] 	= DECL_VAR_II;
		syntacticTable[DECL_VAR_II][getTokenId("caractere")] 	= DECL_VAR_II;
		syntacticTable[DECL_VAR_II][getTokenId("cadeia")] 	= DECL_VAR_II;
		syntacticTable[DECL_VAR_II][getTokenId("booleano")] 	= DECL_VAR_II;
		
		//INICIO GRAMATIC DECL_FUNC
		fillRow(DECL_FUNC, EPSILON);
		syntacticTable[DECL_FUNC][getTokenId("funcao")]	= DECL_FUNC;
		
		fillRow(DECL_FUNC_I, DECL_FUNC_I_R);
		syntacticTable[DECL_FUNC_I][TokenFactory.IDENT] = DECL_FUNC_I_V;
		
		fillRow(PARAMETROS_I, EPSILON);
		syntacticTable[PARAMETROS_I][getTokenId(",")] = PARAMETROS_I;
		
		fillRow(RETORNO_FUNC, RETORNO_FUNC);
		
		//INICIO DECL_MAIN
		fillRow(DECL_MAIN, DECL_MAIN);
		
		//GRAMATICA PARAMETROS
		fillRow(PARAMETROS, EPSILON);
		syntacticTable[PARAMETROS][getTokenId("inteiro")] 	= PARAMETROS;
		syntacticTable[PARAMETROS][getTokenId("real")] 		= PARAMETROS;
		syntacticTable[PARAMETROS][getTokenId("caractere")] = PARAMETROS;
		syntacticTable[PARAMETROS][getTokenId("cadeia")] 	= PARAMETROS;
		syntacticTable[PARAMETROS][getTokenId("booleano")] 	= PARAMETROS;
		
		//INICIO DA GRAMATICA DE VALOR
		fillRow(VALOR, VALOR);
		
		fillRow(EXPRESSAO_CONJUNTA, EXPRESSAO_CONJUNTA);
		fillRow(EXPRESSAO_CONJUNTA_I, EPSILON);
		syntacticTable[EXPRESSAO_CONJUNTA_I][getTokenId("ou")] = EXPRESSAO_CONJUNTA_I;
		
		fillRow(EXPRESSAO_RELACIONAL, EXPRESSAO_RELACIONAL);
		fillRow(EXPRESSAO_RELACIONAL_I, EPSILON);
		syntacticTable[EXPRESSAO_RELACIONAL_I][getTokenId("e")] = EXPRESSAO_RELACIONAL_I;
		
		fillRow(OPERAR_RELACIONALMENTE, EPSILON);
		syntacticTable[OPERAR_RELACIONALMENTE][getTokenId(">")] = OPERAR_RELACIONALMENTE;
		syntacticTable[OPERAR_RELACIONALMENTE][getTokenId(">=")] = OPERAR_RELACIONALMENTE;
		syntacticTable[OPERAR_RELACIONALMENTE][getTokenId("<")] = OPERAR_RELACIONALMENTE;
		syntacticTable[OPERAR_RELACIONALMENTE][getTokenId("<=")] = OPERAR_RELACIONALMENTE;
		syntacticTable[OPERAR_RELACIONALMENTE][getTokenId("<>")] = OPERAR_RELACIONALMENTE;
		
		syntacticTable[OPERAR_RELACIONALMENTE_CONSUME][getTokenId(">")] = CONSUME_GT;
		syntacticTable[OPERAR_RELACIONALMENTE_CONSUME][getTokenId(">=")] = CONSUME_GE;
		syntacticTable[OPERAR_RELACIONALMENTE_CONSUME][getTokenId("<")] = CONSUME_LT;
		syntacticTable[OPERAR_RELACIONALMENTE_CONSUME][getTokenId("<=")] = CONSUME_LE;
		syntacticTable[OPERAR_RELACIONALMENTE_CONSUME][getTokenId("<>")] = CONSUME_DIF;
		
		fillRow(NOT_OPC, EPSILON);
		syntacticTable[NOT_OPC][getTokenId("nao")] = NOT_OPC;
		
		
		//INICIO DA GRAMATICA DE EXPR_SIMPLES
		fillRow(EXPR_SIMPLES, EXPR_SIMPLES);
		fillRow(OPERADOR_MAIS_MENOS, EPSILON);
		syntacticTable[OPERADOR_MAIS_MENOS][getTokenId("+")] = PLUS_CONSUME;
		syntacticTable[OPERADOR_MAIS_MENOS][getTokenId("-")] = MINUS_CONSUME;
		
		fillRow(TERMO, TERMO);
		
		fillRow(TERMO_I, EPSILON);
		syntacticTable[TERMO_I][getTokenId("+")] = TERMO_I;
		syntacticTable[TERMO_I][getTokenId("-")] = TERMO_I;
		
		syntacticTable[FATOR][TokenFactory.IDENT] 	= IDENTIFICADOR_FUNCAO;
		syntacticTable[FATOR][getTokenId("<")] 		= ARRAY_IDENTIFICADOR;
		syntacticTable[FATOR][TokenFactory.NUM_CONST] = CONSUME_NUM_CONST;
		syntacticTable[FATOR][getTokenId("verdadeiro")] = CONSUME_TRUE;
		syntacticTable[FATOR][getTokenId("falso")] = CONSUME_FALSE;
		syntacticTable[FATOR][TokenFactory.CHAR_CONST] = CONSUME_CHAR_CONST;
		syntacticTable[FATOR][TokenFactory.STRING_CONST] = CONSUME_STRING_CONST;
		syntacticTable[FATOR][getTokenId("(")] = PAR_VALOR_PAR; 
		
		fillRow(FATOR_I, EPSILON);
		syntacticTable[FATOR_I][getTokenId("*")] = FATOR_I;
		syntacticTable[FATOR_I][getTokenId("/")] = FATOR_I;
		syntacticTable[FATOR_I_MD][getTokenId("*")] = CONSUME_MULT;
		syntacticTable[FATOR_I_MD][getTokenId("/")] = CONSUME_DIV;
		
		fillRow(POSSIBLE_FUNC, EPSILON);
		syntacticTable[POSSIBLE_FUNC][getTokenId("(")] = POSSIBLE_FUNC;
		
		fillRow(PASSA_PARAM, EPSILON);
		syntacticTable[PASSA_PARAM][getTokenId("+")] = PASSA_PARAM;
		syntacticTable[PASSA_PARAM][getTokenId("-")] = PASSA_PARAM;
		syntacticTable[PASSA_PARAM][getTokenId("(")] = PASSA_PARAM;
		syntacticTable[PASSA_PARAM][getTokenId("nao")] = PASSA_PARAM;
		syntacticTable[PASSA_PARAM][getTokenId("<")] = PASSA_PARAM;
		syntacticTable[PASSA_PARAM][getTokenId("verdadeiro")] = PASSA_PARAM;
		syntacticTable[PASSA_PARAM][getTokenId("falso")] = PASSA_PARAM;
		syntacticTable[PASSA_PARAM][TokenFactory.IDENT] = PASSA_PARAM;
		syntacticTable[PASSA_PARAM][TokenFactory.CHAR_CONST] = PASSA_PARAM;
		syntacticTable[PASSA_PARAM][TokenFactory.NUM_CONST] = PASSA_PARAM;
		syntacticTable[PASSA_PARAM][TokenFactory.STRING_CONST] = PASSA_PARAM;
		
		fillRow(PASSA_PARAM_I, EPSILON);
		syntacticTable[PASSA_PARAM_I][getTokenId(",")] = PASSA_PARAM_I;
		
		fillRow(ARRAY, EPSILON);
		syntacticTable[ARRAY][getTokenId("<")] = ARRAY;
		
		fillRow(ARRAY_I, EPSILON);
		syntacticTable[ARRAY_I][getTokenId(",")] = ARRAY_I;
		
		//ARRAY_PARAM
		fillRow(ARRAY_PARAM, EPSILON);
		syntacticTable[ARRAY_PARAM][getTokenId("<")] = ARRAY_PARAM;
		
		fillRow(ARRAY_INDEXES_OPT, EPSILON);
		syntacticTable[ARRAY_INDEXES_OPT][getTokenId("+")] = ARRAY_INDEXES_OPT;
		syntacticTable[ARRAY_INDEXES_OPT][getTokenId("-")] = ARRAY_INDEXES_OPT;
		syntacticTable[ARRAY_INDEXES_OPT][getTokenId("(")] = ARRAY_INDEXES_OPT;
		syntacticTable[ARRAY_INDEXES_OPT][getTokenId("nao")] = ARRAY_INDEXES_OPT;
		syntacticTable[ARRAY_INDEXES_OPT][getTokenId("<")] = ARRAY_INDEXES_OPT;
		syntacticTable[ARRAY_INDEXES_OPT][getTokenId("verdadeiro")] = ARRAY_INDEXES_OPT;
		syntacticTable[ARRAY_INDEXES_OPT][getTokenId("falso")] = ARRAY_INDEXES_OPT;
		syntacticTable[ARRAY_INDEXES_OPT][TokenFactory.IDENT] = ARRAY_INDEXES_OPT;
		syntacticTable[ARRAY_INDEXES_OPT][TokenFactory.CHAR_CONST] = ARRAY_INDEXES_OPT;
		syntacticTable[ARRAY_INDEXES_OPT][TokenFactory.NUM_CONST] = ARRAY_INDEXES_OPT;
		syntacticTable[ARRAY_INDEXES_OPT][TokenFactory.STRING_CONST] = ARRAY_INDEXES_OPT;
		
		fillRow(ARRAY_PARAM_I, EPSILON);
		syntacticTable[ARRAY_PARAM_I][getTokenId(",")] = ARRAY_PARAM_I;
		
		
		//TYPE GRAMAR
		syntacticTable[TYPE][getTokenId("inteiro")] 	= INTEGER_CONSUME;
		syntacticTable[TYPE][getTokenId("real")] 		= FLOAT_CONSUME;
		syntacticTable[TYPE][getTokenId("caractere")]	= CHAR_CONSUME;
		syntacticTable[TYPE][getTokenId("cadeia")] 		= STRING_CONSUME;
		syntacticTable[TYPE][getTokenId("booleano")] 	= BOOL_CONSUME;
		
		//fillRow(DECL_CONST_VAR_DERIVA, EPSILON);
		syntacticTable[DECL_CONST_VAR_DERIVA][getTokenId("funcao")]	= DECL_FUNC;
		syntacticTable[DECL_CONST_VAR_DERIVA][getTokenId("var")] 	= INICIO_VAR_FUNC;
	}
	
	public void fillRow(int row, int value) {
		for (int i = 0; i < 800; i++)
			syntacticTable[row][i] = value;
	}
	
	public void prepareStack() {
		stack.push(END);
		stack.push(START);
	}
	
	public void startAnalysis() {
		Token token = currentToken();
		while (!stack.isEmpty()) {
			if (token.getId() == stack.peek()) {
				Debug.println("Token consumed: " + token.getLexem());
				stack.pop();
				currentToken++;
				token = currentToken();
			} else {
				int production = syntacticTable[stack.peek()][token.getId()];
				Debug.println("Shift-> Generates production: " + production + "\tState: " + stack.peek());
				
				if (!generateProduction(production)) {
					Debug.println("Expected: " + TokenFactory.meaning_messages.get(stack.peek()) + " but was: " + token.getLexem());
					return;
				}
					
			}
		}
		
		Debug.println("Success!");
	}

	private boolean generateProduction(int production) {
		if (production == -1)
			return false;
			
		stack.pop();
		
		if (production == INICIO_CONST_K_FUNC)
			_Inicio_Const_Var_Func();
		else if (production == INICIO_VAR_FUNC)
			_Inicio_Var_Func();
		else if (production == INICIO_FUNC)
			_Inicio_Func();
		else if (production == DECL_CONST)
			_Decl_Const();
		else if (production == DECL_MAIN)
			_Decl_Main();
		else if (production == DECL_CONST_CONTINUO)
			_Decl_Const_Continuo();
		else if (production == DECL_VAR)
			_Decl_Var();
		else if (production == DECL_VAR_CONTINUO)
			_Decl_Var_Continuo();
		else if (production == DECL_VAR_I)
			_Decl_Var_I();
		else if (production == DECL_VAR_II)
			_Decl_Var_II();
		else if (production == _CONST_VAR_FUNC)
			_Const_Var_Func();
		else if (production == DECL_CONST_I)
			_Decl_Const_I();
		else if (production == DECL_CONST_II)
			_Decl_Const_II();
		else if (production == DECL_FUNC)
			_Decl_Func();
		else if (production == DECL_FUNC_I_V)
			__Decl_Func_I_Void();
		else if (production == DECL_FUNC_I_R)
			__Decl_Func_I_Retr();
		else if (production == RETORNO_FUNC)
			_Retorno_Func();
		else if (production == PARAMETROS)
			_Parametros();
		else if (production == PARAMETROS_I)
			_Parametros_I();
		else if (production == ARRAY_PARAM)
			_Array_Param();
		else if (production == ARRAY_INDEXES_OPT)
			_Array_Indexes_Opt();
		else if (production == ARRAY_PARAM_I)
			_Array_Param_I();
		else if (production == VALOR)
			_Valor();
		else if (production == EXPRESSAO_CONJUNTA)
			_Expressao_Conjunta();
		else if (production == EXPRESSAO_RELACIONAL)
			_Expressao_Relacional();
		else if (production == NOT_OPC)
			_Not_Opc();
		else if (production == EXPR_SIMPLES)
			_Expr_Simples();
		else if (production == TERMO)
			_Termo();
		else if (production == IDENTIFICADOR_FUNCAO)
			__Identificador_Funcao();
		else if (production == ARRAY_IDENTIFICADOR)
			__Array_Identificador();
		else if (production == PAR_VALOR_PAR)
			__Par_Valor_Par();
		else if (production == POSSIBLE_FUNC)
			_Possible_Func();
		else if (production == PASSA_PARAM)
			_Passa_Param();
		else if (production == PASSA_PARAM_I)
			_Passa_Param_I();
		else if (production == ARRAY)
			_Array();
		else if (production == ARRAY_I)
			_Array_I();
		else if (production == FATOR_I)
			_Fator_I();
		else if (production == TERMO_I)
			_Termo_I();
		else if (production == OPERAR_RELACIONALMENTE)
			_Operar_Relacionalmente();
		else if (production == EXPRESSAO_RELACIONAL_I)
			_Expressao_Relacional_I();
		else if (production == EXPRESSAO_CONJUNTA_I)
			_Expressao_Conjunta_I();
		
		
		else if (production == EPSILON)
			_Epsilon();
		else if (production == INTEGER_CONSUME)
			__Integer_Consume();
		else if (production == FLOAT_CONSUME)
			__Float_Consume();
		else if (production == CHAR_CONSUME)
			__Char_Consume();
		else if (production == STRING_CONSUME)
			__String_Consume();
		else if (production == BOOL_CONSUME)
			__Bool_Consume();
		else if (production == PLUS_CONSUME)
			__Plus_Consume();
		else if (production == MINUS_CONSUME)
			__Minus_Consume();
		else if (production == CONSUME_NUM_CONST)
			__Num_Const_Consume();
		else if (production == CONSUME_CHAR_CONST)
			__Char_Const_Consume();
		else if (production == CONSUME_STRING_CONST)
			__String_Const_Consume();
		else if (production == CONSUME_TRUE)
			__True_Consume();
		else if (production == CONSUME_FALSE)
			__False_Consume();
		else if (production == CONSUME_MULT)
			__Consume_Mult();
		else if (production == CONSUME_DIV)
			__Consume_Div();
		else if (production == CONSUME_GT)
			__Consume_GreaterThan();
		else if (production == CONSUME_GE)
			__Consume_GreaterEquals();
		else if (production == CONSUME_LT)
			__Consume_LowerThan();
		else if (production == CONSUME_LE)
			__Consume_LowerEquals();
		else if (production == CONSUME_DIF)
			__Consume_Different();
		
		return true;
	}

	private void _Inicio_Const_Var_Func() {
		stack.push(DECL_CONST_VAR_DERIVA);
		stack.push(DECL_CONST);
	}
	
	private void _Inicio_Var_Func() {
		stack.push(DECL_MAIN);
		stack.push(DECL_FUNC);
		stack.push(DECL_VAR);
	}
	
	private void _Inicio_Func() {
		stack.push(DECL_MAIN);
		stack.push(DECL_FUNC);
	}
	
	private void _Decl_Main() {
		stack.push(getTokenId("fim"));
		//stack.push(CORPO);
		stack.push(getTokenId("inicio"));
		stack.push(getTokenId("programa"));
	}
	
	private void _Decl_Const() {
		stack.push(getTokenId("fim"));
		stack.push(DECL_CONST_CONTINUO);
		stack.push(getTokenId("inicio"));
		stack.push(getTokenId("const"));
	}
	
	private void _Decl_Const_Continuo() {
		stack.push(DECL_CONST_II);
		stack.push(getTokenId(";"));
		stack.push(DECL_CONST_I);
		stack.push(VALOR); 
		stack.push(getTokenId("="));
		stack.push(TokenFactory.IDENT);
		stack.push(TYPE);
	}
	
	private void _Decl_Const_I() {
		stack.push(DECL_CONST_I);
		stack.push(VALOR);
		stack.push(getTokenId("="));
		stack.push(TokenFactory.IDENT);
		stack.push(getTokenId(","));
	}
	
	private void _Decl_Const_II() {
		stack.push(DECL_CONST_II);
		stack.push(getTokenId(";"));
		stack.push(DECL_CONST_I);
		stack.push(VALOR);
		stack.push(getTokenId("="));
		stack.push(TokenFactory.IDENT);
		stack.push(TYPE);
	}
	
	private void _Const_Var_Func() {
		stack.push(DECL_MAIN);
		stack.push(DECL_FUNC);
		stack.push(DECL_VAR);
	}
	
	private void _Decl_Var() {
		stack.push(getTokenId("fim"));
		stack.push(DECL_VAR_CONTINUO);
		stack.push(getTokenId("inicio"));
		stack.push(getTokenId("var"));
	}
	
	private void _Decl_Var_Continuo() {
		stack.push(DECL_VAR_II);
		stack.push(getTokenId(";"));
		stack.push(DECL_VAR_I);
		stack.push(TokenFactory.IDENT);
		stack.push(ARRAY);
		stack.push(TYPE);
	}
	
	private void _Decl_Var_I() {
		stack.push(DECL_VAR_I);
		stack.push(TokenFactory.IDENT);
		stack.push(ARRAY);
		stack.push(getTokenId(","));
	}
	
	private void _Decl_Var_II() {
		stack.push(DECL_VAR_II);
		stack.push(getTokenId(";"));
		stack.push(DECL_VAR_I);
		stack.push(TokenFactory.IDENT);
		stack.push(ARRAY);
		stack.push(TYPE);
	}
	
	private void _Decl_Func() {
		stack.push(DECL_FUNC);
		stack.push(DECL_FUNC_I);
		stack.push(getTokenId("funcao"));
	}
	
	private void __Decl_Func_I_Void() {
		stack.push(getTokenId("fim"));
		//stack.push(CORPO); //TODO Corpo
		stack.push(getTokenId("inicio"));
		stack.push(getTokenId(")"));
		stack.push(PARAMETROS);
		stack.push(getTokenId("("));
		stack.push(TokenFactory.IDENT);
	}
	
	private void __Decl_Func_I_Retr() {
		stack.push(getTokenId("fim"));
		stack.push(RETORNO_FUNC);
		//stack.push(CORPO); //TODO Corpo
		stack.push(getTokenId("inicio"));
		stack.push(getTokenId(")"));
		stack.push(PARAMETROS);
		stack.push(getTokenId("("));
		stack.push(TokenFactory.IDENT);
		stack.push(TYPE);
	}
	
	private void _Retorno_Func() {
		stack.push(getTokenId(";"));
		stack.push(getTokenId(">"));
		stack.push(getTokenId("="));
		stack.push(VALOR);
		stack.push(getTokenId(">"));
		stack.push(getTokenId("="));
	}
	
	private void _Parametros() {
		stack.push(PARAMETROS_I);
		stack.push(TokenFactory.IDENT);
		stack.push(ARRAY_PARAM);
		stack.push(TYPE);
	}
	
	private void _Parametros_I() {
		stack.push(PARAMETROS_I);
		stack.push(TokenFactory.IDENT);
		stack.push(ARRAY_PARAM);
		stack.push(TYPE);
		stack.push(getTokenId(","));
	}
	
	private void _Valor() {
		stack.push(EXPRESSAO_CONJUNTA_I);
		stack.push(EXPRESSAO_CONJUNTA);
	}
	
	private void _Expressao_Conjunta() {
		stack.push(EXPRESSAO_RELACIONAL_I);
		stack.push(EXPRESSAO_RELACIONAL);
	}
	
	private void _Expressao_Conjunta_I() {
		stack.push(EXPRESSAO_CONJUNTA_I);
		stack.push(EXPRESSAO_CONJUNTA);
		stack.push(getTokenId("ou"));
	}
	
	private void _Expressao_Relacional() {
		stack.push(OPERAR_RELACIONALMENTE);
		stack.push(EXPR_SIMPLES);
		stack.push(NOT_OPC);
	}
	
	private void _Expressao_Relacional_I() {
		stack.push(EXPRESSAO_RELACIONAL_I);
		stack.push(EXPRESSAO_RELACIONAL);
		stack.push(getTokenId("e"));
	}
	
	private void _Operar_Relacionalmente() {
		stack.push(EXPR_SIMPLES);
		stack.push(NOT_OPC);
		stack.push(OPERAR_RELACIONALMENTE_CONSUME);
	}
	
	private void _Not_Opc() {
		stack.push(NOT_OPC);
		stack.push(getTokenId("nao"));
	}
	
	private void _Expr_Simples() {
		stack.push(TERMO_I);
		stack.push(TERMO);
		stack.push(OPERADOR_MAIS_MENOS);
	}
	
	private void _Termo() {
		stack.push(FATOR_I);
		stack.push(FATOR);
	}
	
	private void _Termo_I() {
		stack.push(TERMO_I);
		stack.push(TERMO);
		stack.push(OPERADOR_MAIS_MENOS);
	}
	
	private void __Identificador_Funcao() {
		stack.push(POSSIBLE_FUNC);
		stack.push(TokenFactory.IDENT);
	}
	
	private void _Possible_Func() {
		stack.push(getTokenId(")"));
		stack.push(PASSA_PARAM); //CONTINUE
		stack.push(getTokenId("("));
	}
	
	private void _Passa_Param() {
		stack.push(PASSA_PARAM_I);
		stack.push(VALOR);
	}
	
	private void _Passa_Param_I() {
		stack.push(PASSA_PARAM_I);
		stack.push(VALOR);
		stack.push(getTokenId(","));
	}
	
	private void __Array_Identificador() {
		stack.push(TokenFactory.IDENT);
		stack.push(getTokenId(">"));
		stack.push(getTokenId(">"));
		stack.push(ARRAY_I);
		stack.push(EXPR_SIMPLES);
		stack.push(getTokenId("<"));
		stack.push(getTokenId("<"));
	}
	
	private void _Array() {
		stack.push(getTokenId(">"));
		stack.push(getTokenId(">"));
		stack.push(ARRAY_I);
		stack.push(EXPR_SIMPLES);
		stack.push(getTokenId("<"));
		stack.push(getTokenId("<"));
	}
	
	private void _Array_I() {
		stack.push(ARRAY_I);
		stack.push(EXPR_SIMPLES);
		stack.push(getTokenId(","));
	}
	
	private void _Array_Param() {
		stack.push(getTokenId(">"));
		stack.push(getTokenId(">"));
		stack.push(ARRAY_PARAM_I);
		stack.push(ARRAY_INDEXES_OPT);
		stack.push(getTokenId("<"));
		stack.push(getTokenId("<"));
	}
	
	private void _Array_Indexes_Opt() {
		stack.push(EXPR_SIMPLES);
	}
	
	private void _Array_Param_I() {
		stack.push(ARRAY_PARAM_I);
		stack.push(ARRAY_INDEXES_OPT);
		stack.push(getTokenId(","));
	}
	
	private void __Par_Valor_Par() {
		stack.push(getTokenId(")"));
		stack.push(VALOR);
		stack.push(getTokenId("("));
	}
	
	private void _Fator_I() {
		stack.push(FATOR_I);
		stack.push(FATOR);
		stack.push(FATOR_I_MD);
	}
	
	private void __Integer_Consume() {
		stack.push(getTokenId("inteiro"));
	}
	
	private void __Float_Consume() {
		stack.push(getTokenId("real"));
	}
	
	private void __Char_Consume() {
		stack.push(getTokenId("caractere"));
	}
	
	private void __String_Consume() {
		stack.push(getTokenId("cadeia"));
	}
	
	private void __Bool_Consume() {
		stack.push(getTokenId("booleano"));
	}
	
	private void __Plus_Consume() {
		stack.push(getTokenId("+"));
	}
	
	private void __Minus_Consume() {
		stack.push(getTokenId("-"));
	}
	
	private void __Num_Const_Consume() {
		stack.push(TokenFactory.NUM_CONST);
	}
	
	private void __Char_Const_Consume() {
		stack.push(TokenFactory.CHAR_CONST);		
	}
	
	private void __String_Const_Consume() {
		stack.push(TokenFactory.STRING_CONST);
	}
	
	private void __True_Consume() {
		stack.push(getTokenId("verdadeiro"));
	}
	
	private void __False_Consume() {
		stack.push(getTokenId("falso"));
	}
	
	private void __Consume_Mult() {
		stack.push(getTokenId("*"));
	}
	
	private void __Consume_Div() {
		stack.push(getTokenId("/"));
	}
	
	private void __Consume_GreaterThan() {
		stack.push(getTokenId(">"));
	}
	
	private void __Consume_GreaterEquals() {
		stack.push(getTokenId(">="));
	}
	
	private void __Consume_LowerThan() {
		stack.push(getTokenId("<"));
	}
	
	private void __Consume_LowerEquals() {
		stack.push(getTokenId("<="));
	}
	
	private void __Consume_Different() {
		stack.push(getTokenId("<>"));
	}
	
	private void _Epsilon() {
		Debug.println("Reduce");
	}

	public Integer currentTokenId() {
		return currentToken().getId();
	}

	public Token currentToken() {
		if (currentToken < tokens.size())
			return tokens.get(currentToken);
		return new Token(END);
	}

}
