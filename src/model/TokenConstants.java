package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public interface TokenConstants {
	public List<String> reserved_words = new ArrayList<>();
	public HashMap<Integer, String> meaning_messages = new HashMap<>();
	
	public Pattern digit 		= Pattern.compile("[0-9]");
	public Pattern letter 		= Pattern.compile("[a-zA-Z]");
	public Pattern number 		= Pattern.compile(digit + "+(." + digit +"+)?");
	public Pattern identifier	= Pattern.compile(letter + "[" + letter + digit + "_]*");
	public Pattern charactere 	= Pattern.compile("'[" + letter + digit + "]'");
	public Pattern string 		= Pattern.compile("\"" + letter + "([" + letter + digit + " ])*\"");
	
	public int IDENT = 70;
	public int NUM_CONST = 71;
	public int CHAR_CONST = 72;
	public int STRING_CONST = 73;
	
	public int LEX_ERROR_MALFORM_NUM = 140;
	public int LEX_ERROR_MALFORM_STR = 141;
	public int LEX_ERROR_MALFORM_CHR = 142;
	public int LEX_ERROR_INVALID_SYM = 143;
	public int LEX_ERROR_COMMENT_END = 144;
	
	public Pattern malform_number 			= Pattern.compile(digit + "+[.]");
	public Pattern malform_string			= Pattern.compile("\"");
	public Pattern malform_char 			= Pattern.compile("\'");

}