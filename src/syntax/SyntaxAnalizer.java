package syntax;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import debug.Debug;
import model.Token;
import model.TokenFactory;
import syntax_tree.AbstractSyntaxTree;
import syntax_tree.Node;
import syntax_util.SyntaxUtil;

public class SyntaxAnalizer extends SyntaxUtil {
	private List<Token> tokens;
	private List<Integer> syncTokens;
	private Stack<Integer> stack;
	int[][] syntaxTable;
	private int currentToken;
	private AbstractSyntaxTree syntaxTree;
	private Node currentNode;
	
	public SyntaxAnalizer(List<Token> tokens) {
		super();
		this.tokens = tokens;
		this.stack = new Stack<>();
		this.syncTokens = new ArrayList<>();
		syntaxTable = new int[800][800];
		currentToken = 0;
		syntaxTree = new AbstractSyntaxTree();
		
		initializeSyncSymbols();
		initializeTable();
		prepareTree();
		prepareTable();
		prepareStack();
	}

	public void prepareTree() {
		currentNode = new Node();
		syntaxTree.setRoot(currentNode);
	}

	public void initializeSyncSymbols() {
		syncTokens.add(getTokenId(";"));
		syncTokens.add(END);
		syncTokens.add(getTokenId("("));
		syncTokens.add(getTokenId(")"));
		syncTokens.add(getTokenId("inicio"));
		syncTokens.add(getTokenId("fim"));
	}

	public void initializeTable() {
		for (int i = 0; i < 800; i++)
			for (int j = 0; j < 800; j++)
				syntaxTable[i][j] = -1;
	}
	
	
	public void fillRow(int row, int value) {
		for (int i = 0; i < 800; i++)
			syntaxTable[row][i] = value;
	}
	
	public void prepareStack() {
		stack.push(END);
		stack.push(START);
	}
	
	private void childNode(Token token) {
		//System.out.println("Terminal Added to Node: " + currentNode.getType());
		currentNode.addChild(new Node(currentNode, token.getId(), token));
	}
	
	private void createNode(int state) {
		//System.out.println("New Node from state: " + state);
		Node node = new Node(currentNode, state);
		currentNode.addChild(node);
		currentNode = node;
	}
	
	private void returnToFather() {
		//System.out.println("Return to Father Node: " + currentNode.getFather().getType());
		currentNode = currentNode.getFather();
	}
	
	public void startAnalysis() {
		while (!stack.isEmpty()) {
			Token token = currentToken();
			
			if (stack.peek() == FATHER_RETURN) {
				returnToFather();
				stack.pop();
				continue;
			}
				
			//System.out.println("Stack: " + stack);
			//System.out.println("Token is: " + token.getId());
			if (token.getId() == stack.peek()) {
				Debug.println("Token consumed: " + token.getLexem());
				stack.pop();
				childNode(token);
				currentToken++;
				token = currentToken();
			} else {
				int production = syntaxTable[stack.peek()][token.getId()];
				Debug.println("Generates production: " + production + "\tState: " + stack.peek());
				
				if (!generateProduction(production)) {
					showError(stack.peek(), token);
					
					errorRecovery();
					//return;
				}
					
			}
		}
		System.out.println("Success!");
		syntaxTree = syntaxTree.normalize();
		syntaxTree.print();
	}

	private void showError(Integer peek, Token token) {
		if (peek == 400)
			System.out.println("Expected: \"End-Of-File\" but was: " + token.getLexem() + " on line: " + token.getLine());
		else if (peek > 200) {
			System.out.println("-> Expected one of the Following: ");
			for (int k = 0; k < syntaxTable[peek].length; k++)
				if (syntaxTable[peek][k] != -1)
					showError(k, token);
			System.out.println(" ----- \n");
		} else
			System.out.println("Expected: " + TokenFactory.meaning_messages.get(peek) + " but was: " + token.getLexem() + " on line: " + token.getLine());
	}

	private void errorRecovery() {		
		//TODO Error recovery - Isso nao funciona =/ Ou pelo menos nao achei bom
		while (currentTokenId() != 400) {
			if (stack.peek() == 229 && (currentTokenId() == 70 || currentTokenId() == 71 || currentTokenId() == 72 || currentTokenId() == 73 || currentTokenId() == getTokenId("verdadeiro") || currentTokenId() == getTokenId("falso") || currentTokenId() == getTokenId("<")))
				return;
				
			if (stack.peek() == currentTokenId())
				return;
			currentToken++;
		}
		
		while (stack.peek() != 400) {
			for (Integer i : syncTokens)
				if (i == stack.peek())
					break;
			stack.pop();
		}
		
	}

	private boolean generateProduction(int production) {
		if (production == -1)
			return false;
			
		Integer prev = stack.pop();
		createNode(prev);
		
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
		else if (production == CMD_LEIA)
			_Comando_Leia();
		else if (production == LEITURA_I)
			_Leitura_I();
		else if (production == CMD_ESCREVA)
			_Comando_Escreva();
		else if (production == ESCREVIVEL)
			_Escrevivel();
		else if (production == TERMO_E)
			_Termo_E();
		else if (production == TERMO_I_E)
			_Termo_I_E();
		else if (production == FATOR_I_E)
			_Fator_I_E();
		else if (production == PAR_ESC_PAR)
			__Par_Esc_Par();
		else if (production == ESCREVIVEL_I)
			_Escrevivel_I();
		else if (production == CMD_SE)
			_Comando_Se();
		else if (production == ELSE_OPC)
			_Else_Opc();
		else if (production == CMD_ENQUANTO)
			_Comando_Enquanto();
		else if (production == CMD_VAR)
			_Comando_Var();
		else if (production == CMD_ESCOPO)
			_Comando_Novo_Escopo();
		else if (production == CMD_ATTRIB_CHAMA_IDEN)
			_Comando_Attri_To_Ident();
		else if (production == WHOS_NEXT_ATTRIB)
			_Whos_Next_Attrib();
		else if (production == WHOS_NEXT_FUNC)
			_Whos_Next_Func();
		else if (production == CMD_ATTRIB_CHAMA_MATRIZ)
			_Comando_Attri_To_Vect();
		
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
		else if (production == CONSUME_ID_CONST)
			__Identifier_Consume();
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
		stack.push(FATHER_RETURN);
		stack.push(DECL_CONST_VAR_DERIVA);
		stack.push(DECL_CONST);
	}
	
	private void _Inicio_Var_Func() {
		stack.push(FATHER_RETURN);
		stack.push(DECL_MAIN);
		stack.push(DECL_FUNC);
		stack.push(DECL_VAR);
	}
	
	private void _Inicio_Func() {
		stack.push(FATHER_RETURN);
		stack.push(DECL_MAIN);
		stack.push(DECL_FUNC);
	}
	
	private void _Decl_Main() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId("fim"));
		stack.push(CORPO);
		stack.push(getTokenId("inicio"));
		stack.push(getTokenId("programa"));
	}
	
	private void _Decl_Const() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId("fim"));
		stack.push(DECL_CONST_CONTINUO);
		stack.push(getTokenId("inicio"));
		stack.push(getTokenId("const"));
	}
	
	private void _Decl_Const_Continuo() {
		stack.push(FATHER_RETURN);
		stack.push(DECL_CONST_II);
		stack.push(getTokenId(";"));
		stack.push(DECL_CONST_I);
		stack.push(VALOR); 
		stack.push(getTokenId("="));
		stack.push(TokenFactory.IDENT);
		stack.push(TYPE);
	}
	
	private void _Decl_Const_I() {
		stack.push(FATHER_RETURN);
		stack.push(DECL_CONST_I);
		stack.push(VALOR);
		stack.push(getTokenId("="));
		stack.push(TokenFactory.IDENT);
		stack.push(getTokenId(","));
	}
	
	private void _Decl_Const_II() {
		stack.push(FATHER_RETURN);
		stack.push(DECL_CONST_II);
		stack.push(getTokenId(";"));
		stack.push(DECL_CONST_I);
		stack.push(VALOR);
		stack.push(getTokenId("="));
		stack.push(TokenFactory.IDENT);
		stack.push(TYPE);
	}
	
	private void _Const_Var_Func() {
		stack.push(FATHER_RETURN);
		stack.push(DECL_MAIN);
		stack.push(DECL_FUNC);
		stack.push(DECL_VAR);
	}
	
	private void _Decl_Var() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId("fim"));
		stack.push(DECL_VAR_CONTINUO);
		stack.push(getTokenId("inicio"));
		stack.push(getTokenId("var"));
	}
	
	private void _Decl_Var_Continuo() {
		stack.push(FATHER_RETURN);
		stack.push(DECL_VAR_II);
		stack.push(getTokenId(";"));
		stack.push(DECL_VAR_I);
		stack.push(TokenFactory.IDENT);
		stack.push(ARRAY);
		stack.push(TYPE);
	}
	
	private void _Decl_Var_I() {
		stack.push(FATHER_RETURN);
		stack.push(DECL_VAR_I);
		stack.push(TokenFactory.IDENT);
		stack.push(ARRAY);
		stack.push(getTokenId(","));
	}
	
	private void _Decl_Var_II() {
		stack.push(FATHER_RETURN);
		stack.push(DECL_VAR_II);
		stack.push(getTokenId(";"));
		stack.push(DECL_VAR_I);
		stack.push(TokenFactory.IDENT);
		stack.push(ARRAY);
		stack.push(TYPE);
	}
	
	private void _Decl_Func() {
		stack.push(FATHER_RETURN);
		stack.push(DECL_FUNC);
		stack.push(DECL_FUNC_I);
		stack.push(getTokenId("funcao"));
	}
	
	private void __Decl_Func_I_Void() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId("fim"));
		stack.push(CORPO);
		stack.push(getTokenId("inicio"));
		stack.push(getTokenId(")"));
		stack.push(PARAMETROS);
		stack.push(getTokenId("("));
		stack.push(TokenFactory.IDENT);
	}
	
	private void __Decl_Func_I_Retr() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId("fim"));
		stack.push(RETORNO_FUNC);
		stack.push(CORPO);
		stack.push(getTokenId("inicio"));
		stack.push(getTokenId(")"));
		stack.push(PARAMETROS);
		stack.push(getTokenId("("));
		stack.push(TokenFactory.IDENT);
		stack.push(TYPE);
	}
	
	private void _Retorno_Func() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId(";"));
		stack.push(getTokenId(">"));
		stack.push(getTokenId("="));
		stack.push(VALOR);
		stack.push(getTokenId(">"));
		stack.push(getTokenId("="));
	}
	
	private void _Parametros() {
		stack.push(FATHER_RETURN);
		stack.push(PARAMETROS_I);
		stack.push(TokenFactory.IDENT);
		stack.push(ARRAY_PARAM);
		stack.push(TYPE);
	}
	
	private void _Parametros_I() {
		stack.push(FATHER_RETURN);
		stack.push(PARAMETROS_I);
		stack.push(TokenFactory.IDENT);
		stack.push(ARRAY_PARAM);
		stack.push(TYPE);
		stack.push(getTokenId(","));
	}
	
	private void _Valor() {
		stack.push(FATHER_RETURN);
		stack.push(EXPRESSAO_CONJUNTA_I);
		stack.push(EXPRESSAO_CONJUNTA);
	}
	
	private void _Expressao_Conjunta() {
		stack.push(FATHER_RETURN);
		stack.push(EXPRESSAO_RELACIONAL_I);
		stack.push(EXPRESSAO_RELACIONAL);
	}
	
	private void _Expressao_Conjunta_I() {
		stack.push(FATHER_RETURN);
		stack.push(EXPRESSAO_CONJUNTA_I);
		stack.push(EXPRESSAO_CONJUNTA);
		stack.push(getTokenId("ou"));
	}
	
	private void _Expressao_Relacional() {
		stack.push(FATHER_RETURN);
		stack.push(OPERAR_RELACIONALMENTE);
		stack.push(EXPR_SIMPLES);
		stack.push(NOT_OPC);
	}
	
	private void _Expressao_Relacional_I() {
		stack.push(FATHER_RETURN);
		stack.push(EXPRESSAO_RELACIONAL_I);
		stack.push(EXPRESSAO_RELACIONAL);
		stack.push(getTokenId("e"));
	}
	
	private void _Operar_Relacionalmente() {
		stack.push(FATHER_RETURN);
		stack.push(EXPR_SIMPLES);
		stack.push(NOT_OPC);
		stack.push(OPERAR_RELACIONALMENTE_CONSUME);
	}
	
	private void _Not_Opc() {
		stack.push(FATHER_RETURN);
		stack.push(NOT_OPC);
		stack.push(getTokenId("nao"));
	}
	
	private void _Expr_Simples() {
		stack.push(FATHER_RETURN);
		stack.push(TERMO_I);
		stack.push(TERMO);
		stack.push(OPERADOR_MAIS_MENOS);
	}
	
	private void _Termo() {
		stack.push(FATHER_RETURN);
		stack.push(FATOR_I);
		stack.push(FATOR);
	}
	
	private void _Termo_I() {
		stack.push(FATHER_RETURN);
		stack.push(TERMO_I);
		stack.push(TERMO);
		stack.push(OPERADOR_MAIS_MENOS);
	}
	
	private void __Identificador_Funcao() {
		stack.push(FATHER_RETURN);
		stack.push(POSSIBLE_FUNC);
		stack.push(TokenFactory.IDENT);
	}
	
	private void _Possible_Func() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId(")"));
		stack.push(PASSA_PARAM); //CONTINUE
		stack.push(getTokenId("("));
	}
	
	private void _Passa_Param() {
		stack.push(FATHER_RETURN);
		stack.push(PASSA_PARAM_I);
		stack.push(VALOR);
	}
	
	private void _Passa_Param_I() {
		stack.push(FATHER_RETURN);
		stack.push(PASSA_PARAM_I);
		stack.push(VALOR);
		stack.push(getTokenId(","));
	}
	
	private void __Array_Identificador() {
		stack.push(FATHER_RETURN);
		stack.push(TokenFactory.IDENT);
		stack.push(getTokenId(">"));
		stack.push(getTokenId(">"));
		stack.push(ARRAY_I);
		stack.push(EXPR_SIMPLES);
		stack.push(getTokenId("<"));
		stack.push(getTokenId("<"));
	}
	
	private void _Array() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId(">"));
		stack.push(getTokenId(">"));
		stack.push(ARRAY_I);
		stack.push(EXPR_SIMPLES);
		stack.push(getTokenId("<"));
		stack.push(getTokenId("<"));
	}
	
	private void _Array_I() {
		stack.push(FATHER_RETURN);
		stack.push(ARRAY_I);
		stack.push(EXPR_SIMPLES);
		stack.push(getTokenId(","));
	}
	
	private void _Array_Param() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId(">"));
		stack.push(getTokenId(">"));
		stack.push(ARRAY_PARAM_I);
		stack.push(ARRAY_INDEXES_OPT);
		stack.push(getTokenId("<"));
		stack.push(getTokenId("<"));
	}
	
	private void _Array_Indexes_Opt() {
		stack.push(FATHER_RETURN);
		stack.push(EXPR_SIMPLES);
	}
	
	private void _Array_Param_I() {
		stack.push(FATHER_RETURN);
		stack.push(ARRAY_PARAM_I);
		stack.push(ARRAY_INDEXES_OPT);
		stack.push(getTokenId(","));
	}
	
	private void __Par_Valor_Par() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId(")"));
		stack.push(VALOR);
		stack.push(getTokenId("("));
	}
	
	private void _Fator_I() {
		stack.push(FATHER_RETURN);
		stack.push(FATOR_I);
		stack.push(FATOR);
		stack.push(FATOR_I_MD);
	}
	
	private void _Comando_Leia() {
		stack.push(FATHER_RETURN);
		stack.push(CORPO);
		stack.push(getTokenId(";"));
		stack.push(getTokenId(")"));
		stack.push(LEITURA_I);
		stack.push(TokenFactory.IDENT);
		stack.push(ARRAY);
		stack.push(getTokenId("("));
		stack.push(getTokenId("leia"));
	}
	
	private void _Leitura_I() {
		stack.push(FATHER_RETURN);
		stack.push(LEITURA_I);
		stack.push(TokenFactory.IDENT);
		stack.push(ARRAY);
		stack.push(getTokenId(","));
	}
	
	private void _Comando_Escreva() {
		stack.push(FATHER_RETURN);
		stack.push(CORPO);
		stack.push(getTokenId(";"));
		stack.push(getTokenId(")"));
		stack.push(ESCREVIVEL_I);
		stack.push(ESCREVIVEL);
		stack.push(getTokenId("("));
		stack.push(getTokenId("escreva"));
	}
	
	private void _Escrevivel() {
		stack.push(FATHER_RETURN);
		stack.push(TERMO_I_E);
		stack.push(TERMO_E);
		stack.push(OPERADOR_MAIS_MENOS);
	}
	
	private void _Termo_E() {
		stack.push(FATHER_RETURN);
		stack.push(FATOR_I_E);
		stack.push(FATOR_E);
	}
	
	private void _Termo_I_E() {
		stack.push(FATHER_RETURN);
		stack.push(TERMO_I_E);
		stack.push(TERMO_E);
		stack.push(OPERADOR_MAIS_MENOS);
	}
	
	private void _Fator_I_E() {
		stack.push(FATHER_RETURN);
		stack.push(FATOR_I_E);
		stack.push(FATOR_E);
		stack.push(FATOR_I_MD);
	}
	
	private void __Par_Esc_Par() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId(")"));
		stack.push(ESCREVIVEL);
		stack.push(getTokenId("("));
	}
	
	private void _Escrevivel_I() {
		stack.push(FATHER_RETURN);
		stack.push(ESCREVIVEL_I);
		stack.push(ESCREVIVEL);
		stack.push(getTokenId(","));
	}
	
	private void _Comando_Se() {
		stack.push(FATHER_RETURN);
		stack.push(CORPO);
		stack.push(ELSE_OPC);
		stack.push(getTokenId("fim"));
		stack.push(CORPO);
		stack.push(getTokenId("inicio"));
		stack.push(getTokenId("entao"));
		stack.push(getTokenId(")"));
		stack.push(VALOR);
		stack.push(getTokenId("("));
		stack.push(getTokenId("se"));
	}
	
	private void _Else_Opc() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId("fim"));
		stack.push(CORPO);
		stack.push(getTokenId("inicio"));
		stack.push(getTokenId("senao"));
	}
	
	private void _Comando_Enquanto() {
		stack.push(FATHER_RETURN);
		stack.push(CORPO);
		stack.push(getTokenId("fim"));
		stack.push(CORPO);
		stack.push(getTokenId("inicio"));
		stack.push(getTokenId("faca"));
		stack.push(getTokenId(")"));
		stack.push(VALOR);
		stack.push(getTokenId("("));
		stack.push(getTokenId("enquanto"));
	}
	
	private void _Comando_Var() {
		stack.push(FATHER_RETURN);
		stack.push(CORPO);
		stack.push(DECL_VAR);
	}
	
	private void _Comando_Novo_Escopo() {
		stack.push(FATHER_RETURN);
		stack.push(CORPO);
		stack.push(getTokenId("fim"));
		stack.push(CORPO);
		stack.push(getTokenId("inicio"));
	}
	
	private void _Comando_Attri_To_Ident() {
		stack.push(FATHER_RETURN);
		stack.push(CORPO);
		stack.push(WHOS_NEXT);
		stack.push(TokenFactory.IDENT);
	}
	
	private void _Whos_Next_Attrib() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId(";"));
		stack.push(VALOR);
		stack.push(getTokenId("="));
	}
	
	private void _Whos_Next_Func() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId(";"));
		stack.push(getTokenId(")"));
		stack.push(PASSA_PARAM);
		stack.push(getTokenId("("));
	}
	
	private void _Comando_Attri_To_Vect() {
		stack.push(FATHER_RETURN);
		stack.push(CORPO);
		stack.push(getTokenId(";"));
		stack.push(VALOR);
		stack.push(getTokenId("="));
		stack.push(TokenFactory.IDENT);
		stack.push(getTokenId(">"));
		stack.push(getTokenId(">"));
		stack.push(ARRAY_I);
		stack.push(EXPR_SIMPLES);
		stack.push(getTokenId("<"));
		stack.push(getTokenId("<"));
	}
	
	private void __Integer_Consume() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId("inteiro"));
	}
	
	private void __Float_Consume() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId("real"));
	}
	
	private void __Char_Consume() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId("caractere"));
	}
	
	private void __String_Consume() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId("cadeia"));
	}
	
	private void __Bool_Consume() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId("booleano"));
	}
	
	private void __Plus_Consume() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId("+"));
	}
	
	private void __Minus_Consume() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId("-"));
	}
	
	private void __Identifier_Consume() {
		stack.push(FATHER_RETURN);
		stack.push(TokenFactory.IDENT);
	}
	
	private void __Num_Const_Consume() {
		stack.push(FATHER_RETURN);
		stack.push(TokenFactory.NUM_CONST);
	}
	
	private void __Char_Const_Consume() {
		stack.push(FATHER_RETURN);
		stack.push(TokenFactory.CHAR_CONST);		
	}
	
	private void __String_Const_Consume() {
		stack.push(FATHER_RETURN);
		stack.push(TokenFactory.STRING_CONST);
	}
	
	private void __True_Consume() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId("verdadeiro"));
	}
	
	private void __False_Consume() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId("falso"));
	}
	
	private void __Consume_Mult() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId("*"));
	}
	
	private void __Consume_Div() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId("/"));
	}
	
	private void __Consume_GreaterThan() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId(">"));
	}
	
	private void __Consume_GreaterEquals() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId(">="));
	}
	
	private void __Consume_LowerThan() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId("<"));
	}
	
	private void __Consume_LowerEquals() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId("<="));
	}
	
	private void __Consume_Different() {
		stack.push(FATHER_RETURN);
		stack.push(getTokenId("<>"));
	}
	
	private void _Epsilon() {
		stack.push(FATHER_RETURN);
		//Debug.println("Reduce");
	}
	
	public void prepareTable() {
		//INICIO GRAMATICA
		fillRow(START, INICIO_FUNC);
		syntaxTable[START][getTokenId("const")]	= INICIO_CONST_K_FUNC;
		syntaxTable[START][getTokenId("var")] = INICIO_VAR_FUNC;
		syntaxTable[START][getTokenId("funcao")] = INICIO_FUNC;
		
		fillRow(DECL_CONST_VAR_DERIVA, INICIO_FUNC);
		syntaxTable[DECL_CONST_VAR_DERIVA][getTokenId("funcao")] = INICIO_FUNC;
		syntaxTable[DECL_CONST_VAR_DERIVA][getTokenId("var")] = INICIO_VAR_FUNC;
		
		
		
		//INICIO GRAMATICA DECL_CONST
		fillRow(DECL_CONST, DECL_CONST);
		fillRow(DECL_CONST_I, EPSILON);
		syntaxTable[DECL_CONST_I][getTokenId(",")] = DECL_CONST_I;
		//syntaxTable[DECL_CONST_I][getTokenId(";")] 	= EPSILON;
		
		fillRow(DECL_CONST_II, EPSILON);
		syntaxTable[DECL_CONST_II][getTokenId("inteiro")] = DECL_CONST_II;
		syntaxTable[DECL_CONST_II][getTokenId("real")] = DECL_CONST_II;
		syntaxTable[DECL_CONST_II][getTokenId("caractere")] = DECL_CONST_II;
		syntaxTable[DECL_CONST_II][getTokenId("cadeia")] = DECL_CONST_II;
		syntaxTable[DECL_CONST_II][getTokenId("booleano")] = DECL_CONST_II;
		syntaxTable[DECL_CONST_II][getTokenId("fim")] = EPSILON;
		
		fillRow(DECL_CONST_CONTINUO, DECL_CONST_CONTINUO);
		
		//INICIO GRAMATICA DECL_VAR
		fillRow(DECL_VAR, DECL_VAR);
		fillRow(DECL_VAR_CONTINUO, DECL_VAR_CONTINUO);
		
		fillRow(DECL_VAR_I, EPSILON);
		syntaxTable[DECL_VAR_I][getTokenId(",")] = DECL_VAR_I;
		fillRow(DECL_VAR_II, EPSILON);
		syntaxTable[DECL_VAR_II][getTokenId("inteiro")] = DECL_VAR_II;
		syntaxTable[DECL_VAR_II][getTokenId("real")] = DECL_VAR_II;
		syntaxTable[DECL_VAR_II][getTokenId("caractere")] = DECL_VAR_II;
		syntaxTable[DECL_VAR_II][getTokenId("cadeia")] = DECL_VAR_II;
		syntaxTable[DECL_VAR_II][getTokenId("booleano")] = DECL_VAR_II;
		
		//INICIO GRAMATIC DECL_FUNC
		fillRow(DECL_FUNC, EPSILON);
		syntaxTable[DECL_FUNC][getTokenId("funcao")] = DECL_FUNC;
		
		fillRow(DECL_FUNC_I, DECL_FUNC_I_R);
		syntaxTable[DECL_FUNC_I][TokenFactory.IDENT] = DECL_FUNC_I_V;
		
		fillRow(PARAMETROS_I, EPSILON);
		syntaxTable[PARAMETROS_I][getTokenId(",")] = PARAMETROS_I;
		
		fillRow(RETORNO_FUNC, RETORNO_FUNC);
		
		//INICIO DECL_MAIN
		fillRow(DECL_MAIN, DECL_MAIN);
		syntaxTable[DECL_MAIN][getTokenId("programa")] = DECL_MAIN;
		
		
		//GRAMATICA PARAMETROS
		fillRow(PARAMETROS, EPSILON);
		syntaxTable[PARAMETROS][getTokenId("inteiro")] = PARAMETROS;
		syntaxTable[PARAMETROS][getTokenId("real")] = PARAMETROS;
		syntaxTable[PARAMETROS][getTokenId("caractere")] = PARAMETROS;
		syntaxTable[PARAMETROS][getTokenId("cadeia")] = PARAMETROS;
		syntaxTable[PARAMETROS][getTokenId("booleano")] = PARAMETROS;
		
		//INICIO DA GRAMATICA DE VALOR
		fillRow(VALOR, VALOR);
		
		fillRow(EXPRESSAO_CONJUNTA, EXPRESSAO_CONJUNTA);
		fillRow(EXPRESSAO_CONJUNTA_I, EPSILON);
		syntaxTable[EXPRESSAO_CONJUNTA_I][getTokenId("ou")] = EXPRESSAO_CONJUNTA_I;
		
		fillRow(EXPRESSAO_RELACIONAL, EXPRESSAO_RELACIONAL);
		fillRow(EXPRESSAO_RELACIONAL_I, EPSILON);
		syntaxTable[EXPRESSAO_RELACIONAL_I][getTokenId("e")] = EXPRESSAO_RELACIONAL_I;
		
		fillRow(OPERAR_RELACIONALMENTE, EPSILON);
		syntaxTable[OPERAR_RELACIONALMENTE][getTokenId(">")] = OPERAR_RELACIONALMENTE;
		syntaxTable[OPERAR_RELACIONALMENTE][getTokenId(">=")] = OPERAR_RELACIONALMENTE;
		syntaxTable[OPERAR_RELACIONALMENTE][getTokenId("<")] = OPERAR_RELACIONALMENTE;
		syntaxTable[OPERAR_RELACIONALMENTE][getTokenId("<=")] = OPERAR_RELACIONALMENTE;
		syntaxTable[OPERAR_RELACIONALMENTE][getTokenId("<>")] = OPERAR_RELACIONALMENTE;
		
		syntaxTable[OPERAR_RELACIONALMENTE_CONSUME][getTokenId(">")] = CONSUME_GT;
		syntaxTable[OPERAR_RELACIONALMENTE_CONSUME][getTokenId(">=")] = CONSUME_GE;
		syntaxTable[OPERAR_RELACIONALMENTE_CONSUME][getTokenId("<")] = CONSUME_LT;
		syntaxTable[OPERAR_RELACIONALMENTE_CONSUME][getTokenId("<=")] = CONSUME_LE;
		syntaxTable[OPERAR_RELACIONALMENTE_CONSUME][getTokenId("<>")] = CONSUME_DIF;
		
		fillRow(NOT_OPC, EPSILON);
		syntaxTable[NOT_OPC][getTokenId("nao")] = NOT_OPC;
		
		//INICIO DA GRAMATICA DE EXPR_SIMPLES
		fillRow(EXPR_SIMPLES, EXPR_SIMPLES);
		fillRow(OPERADOR_MAIS_MENOS, EPSILON);
		syntaxTable[OPERADOR_MAIS_MENOS][getTokenId("+")] = PLUS_CONSUME;
		syntaxTable[OPERADOR_MAIS_MENOS][getTokenId("-")] = MINUS_CONSUME;
		
		fillRow(TERMO, TERMO);
		
		fillRow(TERMO_I, EPSILON);
		syntaxTable[TERMO_I][getTokenId("+")] = TERMO_I;
		syntaxTable[TERMO_I][getTokenId("-")] = TERMO_I;
		
		syntaxTable[FATOR][TokenFactory.IDENT] = IDENTIFICADOR_FUNCAO;
		syntaxTable[FATOR][getTokenId("<")] = ARRAY_IDENTIFICADOR;
		syntaxTable[FATOR][TokenFactory.NUM_CONST] = CONSUME_NUM_CONST;
		syntaxTable[FATOR][getTokenId("verdadeiro")] = CONSUME_TRUE;
		syntaxTable[FATOR][getTokenId("falso")] = CONSUME_FALSE;
		syntaxTable[FATOR][TokenFactory.CHAR_CONST] = CONSUME_CHAR_CONST;
		syntaxTable[FATOR][TokenFactory.STRING_CONST] = CONSUME_STRING_CONST;
		syntaxTable[FATOR][getTokenId("(")] = PAR_VALOR_PAR; 
		
		fillRow(FATOR_I, EPSILON);
		syntaxTable[FATOR_I][getTokenId("*")] = FATOR_I;
		syntaxTable[FATOR_I][getTokenId("/")] = FATOR_I;
		syntaxTable[FATOR_I_MD][getTokenId("*")] = CONSUME_MULT;
		syntaxTable[FATOR_I_MD][getTokenId("/")] = CONSUME_DIV;
		
		fillRow(POSSIBLE_FUNC, EPSILON);
		syntaxTable[POSSIBLE_FUNC][getTokenId("(")] = POSSIBLE_FUNC;
		
		fillRow(PASSA_PARAM, EPSILON);
		syntaxTable[PASSA_PARAM][getTokenId("+")] = PASSA_PARAM;
		syntaxTable[PASSA_PARAM][getTokenId("-")] = PASSA_PARAM;
		syntaxTable[PASSA_PARAM][getTokenId("(")] = PASSA_PARAM;
		syntaxTable[PASSA_PARAM][getTokenId("nao")] = PASSA_PARAM;
		syntaxTable[PASSA_PARAM][getTokenId("<")] = PASSA_PARAM;
		syntaxTable[PASSA_PARAM][getTokenId("verdadeiro")] = PASSA_PARAM;
		syntaxTable[PASSA_PARAM][getTokenId("falso")] = PASSA_PARAM;
		syntaxTable[PASSA_PARAM][TokenFactory.IDENT] = PASSA_PARAM;
		syntaxTable[PASSA_PARAM][TokenFactory.CHAR_CONST] = PASSA_PARAM;
		syntaxTable[PASSA_PARAM][TokenFactory.NUM_CONST] = PASSA_PARAM;
		syntaxTable[PASSA_PARAM][TokenFactory.STRING_CONST] = PASSA_PARAM;
		
		fillRow(PASSA_PARAM_I, EPSILON);
		syntaxTable[PASSA_PARAM_I][getTokenId(",")] = PASSA_PARAM_I;
		
		fillRow(ARRAY, EPSILON);
		syntaxTable[ARRAY][getTokenId("<")] = ARRAY;
		
		fillRow(ARRAY_I, EPSILON);
		syntaxTable[ARRAY_I][getTokenId(",")] = ARRAY_I;
		
		//ARRAY_PARAM
		fillRow(ARRAY_PARAM, EPSILON);
		syntaxTable[ARRAY_PARAM][getTokenId("<")] = ARRAY_PARAM;
		
		fillRow(ARRAY_INDEXES_OPT, EPSILON);
		syntaxTable[ARRAY_INDEXES_OPT][getTokenId("+")] = ARRAY_INDEXES_OPT;
		syntaxTable[ARRAY_INDEXES_OPT][getTokenId("-")] = ARRAY_INDEXES_OPT;
		syntaxTable[ARRAY_INDEXES_OPT][getTokenId("(")] = ARRAY_INDEXES_OPT;
		syntaxTable[ARRAY_INDEXES_OPT][getTokenId("nao")] = ARRAY_INDEXES_OPT;
		syntaxTable[ARRAY_INDEXES_OPT][getTokenId("<")] = ARRAY_INDEXES_OPT;
		syntaxTable[ARRAY_INDEXES_OPT][getTokenId("verdadeiro")] = ARRAY_INDEXES_OPT;
		syntaxTable[ARRAY_INDEXES_OPT][getTokenId("falso")] = ARRAY_INDEXES_OPT;
		syntaxTable[ARRAY_INDEXES_OPT][TokenFactory.IDENT] = ARRAY_INDEXES_OPT;
		syntaxTable[ARRAY_INDEXES_OPT][TokenFactory.CHAR_CONST] = ARRAY_INDEXES_OPT;
		syntaxTable[ARRAY_INDEXES_OPT][TokenFactory.NUM_CONST] = ARRAY_INDEXES_OPT;
		syntaxTable[ARRAY_INDEXES_OPT][TokenFactory.STRING_CONST] = ARRAY_INDEXES_OPT;
		
		fillRow(ARRAY_PARAM_I, EPSILON);
		syntaxTable[ARRAY_PARAM_I][getTokenId(",")] = ARRAY_PARAM_I;
		
		
		//INICIO CORPO
		fillRow(CORPO, EPSILON);
		syntaxTable[CORPO][getTokenId("leia")] = CMD_LEIA;
		syntaxTable[CORPO][getTokenId("escreva")] = CMD_ESCREVA;
		syntaxTable[CORPO][getTokenId("se")] = CMD_SE;
		syntaxTable[CORPO][getTokenId("enquanto")] = CMD_ENQUANTO;
		syntaxTable[CORPO][TokenFactory.IDENT] = CMD_ATTRIB_CHAMA_IDEN;
		syntaxTable[CORPO][getTokenId("<")] = CMD_ATTRIB_CHAMA_MATRIZ;
		syntaxTable[CORPO][getTokenId("var")] = CMD_VAR;
		syntaxTable[CORPO][getTokenId("inicio")] = CMD_ESCOPO;
		
		fillRow(LEITURA_I, EPSILON);
		syntaxTable[LEITURA_I][getTokenId(",")] = LEITURA_I;

		//ESCRITA
		fillRow(ESCREVIVEL_I, EPSILON);
		syntaxTable[ESCREVIVEL_I][getTokenId(",")] = ESCREVIVEL_I;
		
		fillRow(ESCREVIVEL, ESCREVIVEL);
		
		//ESCREVIVEL
		fillRow(TERMO_E, TERMO_E);
		
		fillRow(TERMO_I_E, EPSILON);
		syntaxTable[TERMO_I_E][getTokenId("+")] = TERMO_I_E;
		syntaxTable[TERMO_I_E][getTokenId("-")] = TERMO_I_E;
		
		syntaxTable[FATOR_E][getTokenId("(")] = PAR_ESC_PAR;
		syntaxTable[FATOR_E][getTokenId("<")] = ARRAY_IDENTIFICADOR;
		syntaxTable[FATOR_E][TokenFactory.IDENT] = CONSUME_ID_CONST;
		syntaxTable[FATOR_E][TokenFactory.CHAR_CONST] = CONSUME_CHAR_CONST;
		syntaxTable[FATOR_E][TokenFactory.NUM_CONST] = CONSUME_NUM_CONST;
		syntaxTable[FATOR_E][TokenFactory.STRING_CONST] = CONSUME_STRING_CONST;
		
		fillRow(FATOR_I_E, EPSILON);
		syntaxTable[FATOR_I_E][getTokenId("*")] = FATOR_I_E;
		syntaxTable[FATOR_I_E][getTokenId("/")] = FATOR_I_E;
		
		
		//ELSE
		fillRow(ELSE_OPC, EPSILON);
		syntaxTable[ELSE_OPC][getTokenId("senao")] = ELSE_OPC;
		
		syntaxTable[WHOS_NEXT][getTokenId("=")] = WHOS_NEXT_ATTRIB;
		syntaxTable[WHOS_NEXT][getTokenId("(")] = WHOS_NEXT_FUNC;
		
		//TYPE GRAMAR
		syntaxTable[TYPE][getTokenId("inteiro")] = INTEGER_CONSUME;
		syntaxTable[TYPE][getTokenId("real")] = FLOAT_CONSUME;
		syntaxTable[TYPE][getTokenId("caractere")] = CHAR_CONSUME;
		syntaxTable[TYPE][getTokenId("cadeia")] = STRING_CONSUME;
		syntaxTable[TYPE][getTokenId("booleano")] = BOOL_CONSUME;
		
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