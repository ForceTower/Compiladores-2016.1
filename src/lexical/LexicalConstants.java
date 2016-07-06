package lexical;

public interface LexicalConstants {
	public int EOF = 0;
	public int INT = 1;
	public int DOUBLE = 3;
	public int CHAR = 4;
	public int VOID = 6;
	public int LPAREN = 8; // (
	public int RPAREN = 9;
	public int LBRACKET = 10; // [
	public int RBRACKET = 11;
	public int LKEY= 12; // {
	public int RKEY = 13;
	public int COMMA = 14; //,
	public int SEMICOLON = 15;
	public int GREATERTHAN = 16;
	public int LOWERTHAN = 17;
	public int GREATEREQUALS = 18;
	public int LOWEREQUALS = 19;
	public int INTEGER_CONSTANT = 20;
	public int DOUBLE_CONSTANT = 22;
	public int STRING_CONSTANT = 23;
	public int RETURN = 25;
	public int IDENT = 28;
	public int ATTRIB = 29;
	public int EQUALS = 30;
	public int AND = 31;
	public int BIT_AND = 32;
	public int OR = 33;
	public int BIT_OR = 34;
	public int NOTEQUALS = 35;
	public int NOT = 36;
	public int PLUS = 37;
	public int MINUS = 38;
	public int TIMES = 39;
	public int DIVISION = 40;
	public int PERCENT = 41;
	public int IF = 42;
	public int ELSE = 43;
	
	public int automata[][] = 	{
								{ 1,-1, 3, 3, 4, 6, 8,10,12,-1,14,16},
								{ 1, 2,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
								{ 2,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
								{ 3,-1, 3, 3,-1,-1,-1,-1,-1,-1,-1,-1},
								{-1,-1,-1,-1, 5,-1,-1,-1,-1,-1,-1,-1},
								{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
								{-1,-1,-1,-1,-1, 7,-1,-1,-1,-1,-1,-1},
								{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
								{-1,-1,-1,-1,-1,-1, 9,-1,-1,-1,-1,-1},
								{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
								{-1,-1,-1,-1,-1,-1,-1,11,-1,-1,-1,-1},
								{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
								{12,12,12,12,12,12,12,12,13,12,12,12},
								{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
								{-1,-1,-1,-1,-1,-1,-1,15,-1,-1,-1,-1},
								{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
								{-1,-1,-1,-1,-1,-1,-1,17,-1,-1,-1,-1},
								{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},		
								};
}
