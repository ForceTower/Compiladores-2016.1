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
		syntacticTable[DECL_CONST_CONTINUO][getTokenId("inteiro")] 	= DECL_CONST_CONTINUO;
		syntacticTable[DECL_CONST_CONTINUO][getTokenId("real")] 	= DECL_CONST_CONTINUO;
		syntacticTable[DECL_CONST_VAR_DERIVA][getTokenId("var")] 	= _CONST_VAR_FUNC;
		syntacticTable[INICIO_CONST_K_FUNC][getTokenId("funcao")]	= DECL_FUNC;
		syntacticTable[INICIO_CONST_K_FUNC][getTokenId("var")] 		= DECL_VAR;
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
				Debug.println("Generates production: " + production);
				
				if (!generateProduction(production)) {
					Debug.println("Expected: " + TokenFactory.meaning_messages.get(stack.peek()) + " but was: " + token.getLexem());
					//TODO: Handle syntectic errors here
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
		//stack.push(DECL_CONST_II); //TODO Somente para testes
		stack.push(getTokenId(";"));
		//stack.push(DECL_CONST_I); //TODO Somente para testes
		stack.push(TokenFactory.NUM_CONST); //TODO correto é valor, para testes, numero 
		stack.push(getTokenId("="));
		stack.push(TokenFactory.IDENT);
		stack.push(getTokenId("inteiro")); //TODO Correto é tipo, mas para testes, será int
	}
	
	private void _Decl_Const_Var_Deriva() {
		
	}
	
	private void _Const_Var_Func() {
		stack.push(DECL_MAIN);
		stack.push(DECL_FUNC);
		stack.push(DECL_VAR);
	}

	public Integer currentTokenId() {
		return currentToken().getId();
	}

	public Token currentToken() {
		return tokens.get(currentToken);
	}

}
