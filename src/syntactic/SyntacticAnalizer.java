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
		syntacticTable[START][getTokenId("const")]		= INICIO_CONST_K_FUNC;
		syntacticTable[START][getTokenId("var")] 		= INICIO_VAR_FUNC;
		syntacticTable[START][getTokenId("funcao")] 	= INICIO_FUNC;
		syntacticTable[START][getTokenId("programa")] 	= INICIO_FUNC;
		
		syntacticTable[DECL_CONST][getTokenId("const")] = DECL_CONST;
		
		syntacticTable[DECL_CONST_I][getTokenId(",")] 	= DECL_CONST_I;
		syntacticTable[DECL_CONST_I][getTokenId(";")] 	= EPSILON;
		
		syntacticTable[DECL_CONST_II][getTokenId("inteiro")] 	= DECL_CONST_II;//Correto é tipo
		syntacticTable[DECL_CONST_II][getTokenId("real")] 	= DECL_CONST_II;
		syntacticTable[DECL_CONST_II][getTokenId("caractere")] 	= DECL_CONST_II;
		syntacticTable[DECL_CONST_II][getTokenId("cadeia")] 	= DECL_CONST_II;
		syntacticTable[DECL_CONST_II][getTokenId("booleano")] 	= DECL_CONST_II;
		syntacticTable[DECL_CONST_II][getTokenId("fim")] 	= EPSILON;
		
		fillRow(DECL_CONST_CONTINUO, DECL_CONST_CONTINUO);
		//syntacticTable[DECL_CONST_CONTINUO][getTokenId("inteiro")] 	= DECL_CONST_CONTINUO; //Correto é tipo
		//syntacticTable[DECL_CONST_CONTINUO][getTokenId("real")] 	= DECL_CONST_CONTINUO;
		//syntacticTable[DECL_CONST_CONTINUO][getTokenId("caractere")] 	= DECL_CONST_CONTINUO;
		//syntacticTable[DECL_CONST_CONTINUO][getTokenId("cadeia")] 	= DECL_CONST_CONTINUO;
		//syntacticTable[DECL_CONST_CONTINUO][getTokenId("booleano")] 	= DECL_CONST_CONTINUO;
		
		syntacticTable[DECL_CONST_VAR_DERIVA][getTokenId("var")] 	= _CONST_VAR_FUNC;
		
		//nao, '-', '(', '+', '<', Cadeia, Caractere, False, Identificador, Num, True
		fillRow(VALOR, VALOR);
		//syntacticTable[VALOR][getTokenId("+")] = VALOR;
		//syntacticTable[VALOR][getTokenId("-")] = VALOR;
		//syntacticTable[VALOR][getTokenId("(")] = VALOR;
		//syntacticTable[VALOR][getTokenId("nao")] = VALOR;
		//syntacticTable[VALOR][getTokenId("<")] = VALOR;
		//syntacticTable[VALOR][getTokenId("verdadeiro")] = VALOR;
		//syntacticTable[VALOR][getTokenId("falso")] = VALOR;
		//syntacticTable[VALOR][TokenFactory.IDENT] = VALOR;
		//syntacticTable[VALOR][TokenFactory.CHAR_CONST] = VALOR;
		//syntacticTable[VALOR][TokenFactory.NUM_CONST] = VALOR;
		//syntacticTable[VALOR][TokenFactory.STRING_CONST] = VALOR;
		
		fillRow(EXPRESSAO_CONJUNTA, EXPRESSAO_CONJUNTA);
		
		fillRow(EXPRESSAO_RELACIONAL, EXPRESSAO_RELACIONAL);
		
		fillRow(NOT_OPC, EPSILON);
		syntacticTable[NOT_OPC][getTokenId("nao")] = NOT_OPC;
		
		fillRow(EXPR_SIMPLES, EXPR_SIMPLES);
		fillRow(OPERADOR_MAIS_MENOS, EPSILON);
		syntacticTable[OPERADOR_MAIS_MENOS][getTokenId("+")] = PLUS_CONSUME;
		syntacticTable[OPERADOR_MAIS_MENOS][getTokenId("-")] = MINUS_CONSUME;
		
		fillRow(TERMO, TERMO);
		
		syntacticTable[FATOR][TokenFactory.IDENT] 	= IDENTIFICADOR_FUNCAO;
		syntacticTable[FATOR][getTokenId("<")] 		= ARRAY_IDENTIFICADOR;
		syntacticTable[FATOR][TokenFactory.NUM_CONST] = CONSUME_NUM_CONST;
		syntacticTable[FATOR][getTokenId("verdadeiro")] = CONSUME_TRUE;
		syntacticTable[FATOR][getTokenId("falso")] = CONSUME_FALSE;
		syntacticTable[FATOR][TokenFactory.CHAR_CONST] = CONSUME_CHAR_CONST;
		syntacticTable[FATOR][TokenFactory.STRING_CONST] = CONSUME_STRING_CONST;
		syntacticTable[FATOR][getTokenId("(")] = PAR_VALOR_PAR; 
		
		fillRow(POSSIBLE_FUNC, EPSILON);
		syntacticTable[POSSIBLE_FUNC][getTokenId("(")] = POSSIBLE_FUNC;
		
		syntacticTable[TYPE][getTokenId("inteiro")] 	= INTEGER_CONSUME;
		syntacticTable[TYPE][getTokenId("real")] 		= FLOAT_CONSUME;
		syntacticTable[TYPE][getTokenId("caractere")]	= CHAR_CONSUME;
		syntacticTable[TYPE][getTokenId("cadeia")] 		= STRING_CONSUME;
		syntacticTable[TYPE][getTokenId("booleano")] 	= BOOL_CONSUME;
		
		syntacticTable[INICIO_CONST_K_FUNC][getTokenId("funcao")]	= DECL_FUNC;
		syntacticTable[INICIO_CONST_K_FUNC][getTokenId("var")] 		= DECL_VAR;
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
				Debug.println("Token identified: " + token.getLexem());
				stack.pop();
				currentToken++;
				token = currentToken();
			} else {
				int production = syntacticTable[stack.peek()][token.getId()];
				Debug.println("Shift-> Generates production: " + production);
				
				if (!generateProduction(production)) {
					Debug.println("Expected: " + TokenFactory.meaning_messages.get(stack.peek()) + " but was: " + token.getLexem());
					//TODO: Handle syntactic errors here
					return;
				}
					
			}
		}
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
		else if (production == DECL_CONST_CONTINUO)
			_Decl_Const_Continuo();
		else if (production == DECL_CONST_VAR_DERIVA)
			_Decl_Const_Var_Deriva();
		else if (production == _CONST_VAR_FUNC)
			_Const_Var_Func();
		else if (production == DECL_CONST_I)
			_Decl_Const_I();
		else if (production == DECL_CONST_II)
			_Decl_Const_II();
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
	
	private void _Decl_Const() {
		stack.push(getTokenId("fim"));
		stack.push(DECL_CONST_CONTINUO);
		stack.push(getTokenId("inicio"));
		stack.push(getTokenId("const"));
	}
	
	private void _Decl_Const_Continuo() {
		stack.push(DECL_CONST_II); //TODO Somente para testes
		stack.push(getTokenId(";"));
		stack.push(DECL_CONST_I); //TODO Somente para testes
		stack.push(VALOR); //TODO correto é valor, para testes, numero 
		stack.push(getTokenId("="));
		stack.push(TokenFactory.IDENT);
		stack.push(TYPE); //TODO Correto é tipo, mas para testes, será int
	}
	
	private void _Decl_Const_Var_Deriva() {
		Debug.println("Not Implemented Yet");
		System.exit(0);
	}
	
	private void _Decl_Const_I() {
		stack.push(DECL_CONST_I);
		stack.push(VALOR); //TODO correto é valor, para testes, numero 
		stack.push(getTokenId("="));
		stack.push(TokenFactory.IDENT);
		stack.push(getTokenId(","));
	}
	
	private void _Decl_Const_II() {
		stack.push(DECL_CONST_II); //TODO Somente para testes
		stack.push(getTokenId(";"));
		stack.push(DECL_CONST_I); //TODO Somente para testes
		stack.push(VALOR); //TODO correto é valor, para testes, numero 
		stack.push(getTokenId("="));
		stack.push(TokenFactory.IDENT);
		stack.push(TYPE); //TODO Correto é tipo, mas para testes, será int
	}
	
	private void _Const_Var_Func() {
		stack.push(DECL_MAIN);
		stack.push(DECL_FUNC);
		stack.push(DECL_VAR);
	}
	
	/*private void _Valor() {
		stack.push(EXP_RELACIONAL_BOOLEANA);
	}*/
	
	private void _Valor() { //Simplification of state Value ::= Expr_Rel_Booleana
		stack.push(EXPRESSAO_CONJUNTA_I);
		stack.push(EXPRESSAO_CONJUNTA);
	}
	
	private void _Expressao_Conjunta() {
		stack.push(EXPRESSAO_RELACIONAL_I);
		stack.push(EXPRESSAO_RELACIONAL);
	}
	
	private void _Expressao_Relacional() {
		stack.push(OPERAR_RELACIONALMENTE);
		stack.push(EXPR_SIMPLES);
		stack.push(NOT_OPC);
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
	
	private void __Identificador_Funcao() {
		stack.push(POSSIBLE_FUNC);
		stack.push(TokenFactory.IDENT);
	}
	
	private void _Possible_Func() {
		stack.push(getTokenId(")"));
		stack.push(PASSA_PARAM); //CONTINUE
		stack.push(getTokenId("("));
	}
	
	private void __Array_Identificador() {
		stack.push(getTokenId(">"));
		stack.push(getTokenId(">"));
		stack.push(ARRAY_I);
		stack.push(ARRAY_INDEXES);
		stack.push(getTokenId("<"));
		stack.push(getTokenId("<"));
	}
	
	private void __Par_Valor_Par() {
		stack.push(getTokenId(")"));
		stack.push(VALOR);
		stack.push(getTokenId("("));
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
	
	private void _Epsilon() {
		//stack.pop();
		Debug.println("Reduce");
	}

	public Integer currentTokenId() {
		return currentToken().getId();
	}

	public Token currentToken() {
		return tokens.get(currentToken);
	}

}
