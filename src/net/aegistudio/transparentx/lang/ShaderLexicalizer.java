package net.aegistudio.transparentx.lang;

import java.util.ArrayList;

/**
 * Lexicalizer will divide the original source code
 * into several lexical objects with tags, so that 
 * following grammar process can be done.
 * @author aegistudio
 *
 */

public class ShaderLexicalizer {

	Integer[] lexicalDivisons;
	EnumLexicalUnit[] lexicalTags;
	
	ArrayList<Integer> _division;
	ArrayList<EnumLexicalUnit> _tag;
	
	static final int JUMP_BOARD = 0;
	
	static final int DIGIT = 1;
	static final int DECIMALDGT = 2;
	
	static final int IDENTIFIER = 3;
	
	static final int OPERATOR = 4;
	static final int SLASH = 5;
	
	static final int MULTILINE_COMMENT = 6;
	static final int LINE_COMMENT = 7;
	
	static final int DOT = 8;
	
	static final int BLANK = 9;
	static final int SUSPICIOUS_ENDOFMULTILINE = 10;
	
	static final int EXPONENT_SYMBOL = 11;
	static final int EXPONENT = 12;
	
	static final String CH_OPERATOR = "+-*%!=<>(),;[]{}^|&?:";
	static final String CH_BLANK = " \n\r\t";
	
	String source;
	
	public void lexicalize(String sourceCode) throws Exception {
		source = sourceCode;
		char[] sourceArray = sourceCode.toCharArray();
		
		_division = new ArrayList<Integer>();
		_tag = new ArrayList<EnumLexicalUnit>();
		
		int state = JUMP_BOARD;
		
		int i = 0;
		for(; i < sourceArray.length;) {
			char c = sourceArray[i];
			switch(state){
				case JUMP_BOARD:
					if(inside(c, CH_BLANK)) 
						state = BLANK;
					else if(between(c, '0', '9'))
						state = DIGIT;
					else if(beginOfIdentifier(c))
						state = IDENTIFIER;
					else if(c == '/') {
						state = SLASH;
						i ++;
					}
					else if(c == '.') {
						state = DOT;
						i ++;
					}
					else if(i > 0 && inside(c, CH_OPERATOR)) 
						state = OPERATOR;
					else syntaxError(c, i);
				break;
				
				case BLANK:
					if(inside(c, CH_BLANK)) i ++;
					else {
						ack(i - 1, EnumLexicalUnit.BLANK);
						state = JUMP_BOARD;
					}
				break;
				
				case DOT:
					if(inside(c, CH_BLANK)) {
						state = BLANK;
					}
					else if(between(c, '0', '9')) {
						state = DECIMALDGT;
					}
					else if(beginOfIdentifier(c)) {
						ackOperator(i - 1, c);
						state = IDENTIFIER;
					}
					else syntaxError(c, i);
				break;
				
				case DIGIT:
					if(between(c, '0', '9')) i ++;
					else if(c == '.') {
						state = DECIMALDGT;
						i ++;
					}
					else if(inside(c, CH_OPERATOR)) {
						ack(i - 1, EnumLexicalUnit.INTEGER);
						
						state = OPERATOR;
					}
					else if(c == '/') {
						ack(i - 1, EnumLexicalUnit.INTEGER);
						state = SLASH;
					}
					else if(inside(c, "fd")) {
						ack(i, EnumLexicalUnit.FLOAT);
						
						state = JUMP_BOARD;
						i ++;
					}
					else if(inside(c, CH_BLANK)) {
						ack(i, EnumLexicalUnit.INTEGER);
						
						state = BLANK;
					}
					else if(c == 'e' || c == 'E') {
						state = EXPONENT_SYMBOL;
						i ++;
					}
					else syntaxError(c, i);
				break;
				
				case DECIMALDGT:
					if(between(c, '0', '9')) i ++;
					else if(inside(c, "fd")) {
						ack(i, EnumLexicalUnit.FLOAT);
						
						state = JUMP_BOARD;
						i ++;
					}
					else if(inside(c, CH_OPERATOR)) {
						ack(i - 1, EnumLexicalUnit.FLOAT);
						
						state = OPERATOR;
					}
					else if(inside(c, CH_BLANK)) {
						ack(i - 1, EnumLexicalUnit.FLOAT);
						
						state = BLANK;
					}
					else if(c == 'e' || c == 'E') {
						state = EXPONENT_SYMBOL;
						i ++;
					}
					else syntaxError(c, i);
				break;
				
				case EXPONENT_SYMBOL:
					if(between(c, '0', '9')) i ++;
					else if(c == '+' || c == '-') i++;
					else syntaxError(c, i);
					state = EXPONENT;
				break;
				
				case EXPONENT:
					if(between(c, '0', '9')) i ++;
					else if(inside(c, "fd")) {
						ack(i, EnumLexicalUnit.FLOAT);
						
						state = JUMP_BOARD;
						i ++;
					}
					else if(inside(c, CH_OPERATOR)) {
						ack(i - 1, EnumLexicalUnit.FLOAT);
						
						state = OPERATOR;
					}
					else if(inside(c, CH_BLANK)) {
						ack(i - 1, EnumLexicalUnit.FLOAT);
						
						state = BLANK;
					}
					else syntaxError(c, i);
				break;
				
				case OPERATOR:
					ackOperator(i, c);
					state = JUMP_BOARD;
					i ++;
				break;
				
				case SLASH:
					if(c == '/') {
						state = LINE_COMMENT;
						i ++;
					}
					else if(c == '*') {
						state = MULTILINE_COMMENT;
						i ++;
					}
					else {
						ackOperator(i - 1, c);
						state = JUMP_BOARD;
					}
				break;
				
				case IDENTIFIER:
					if(beginOfIdentifier(c) || between(c, '0', '9'))
						i ++;
					else if(inside(c, CH_OPERATOR)) {
						ack(i - 1, EnumLexicalUnit.IDENTIFIER);
						state = JUMP_BOARD;
					}
					else if(c == '.') {
						ack(i - 1, EnumLexicalUnit.IDENTIFIER);
						state = JUMP_BOARD;
					}
					else if(inside(c, CH_BLANK)) {
						ack(i - 1, EnumLexicalUnit.IDENTIFIER);
						state = BLANK;
					}
				break;
				
				case LINE_COMMENT:
					if(c == '\n'){
						ack(i, EnumLexicalUnit.COMMENT);
						state = JUMP_BOARD;
					}
					i ++;
				break;
				
				case MULTILINE_COMMENT:
					if(c == '*')
						state = SUSPICIOUS_ENDOFMULTILINE;
					i ++;
				break;
				
				case SUSPICIOUS_ENDOFMULTILINE:
					if(c == '/') {
						ack(i, EnumLexicalUnit.COMMENT);
						state = JUMP_BOARD;
						i ++;
					}
					else state = MULTILINE_COMMENT;
				break;
				default:
					throw new Exception("Unknowd state!" + state);
			}
		}
		
		switch (state) {
			case IDENTIFIER:
				ack(i - 1, EnumLexicalUnit.IDENTIFIER);
			break;
			case BLANK:
				ack(i - 1, EnumLexicalUnit.BLANK);
			break;
			
			case SLASH:
			case DOT:
			case OPERATOR:
				ackOperator(i - 1, sourceArray[i - 1]);
			break;
			case JUMP_BOARD:
			break;
			
			case LINE_COMMENT:
				ack(i - 1, EnumLexicalUnit.COMMENT);
			break;
			
			case SUSPICIOUS_ENDOFMULTILINE:
			case MULTILINE_COMMENT:
				syntaxError('\0', i - 1);
			break;
			
		}
		
		this.lexicalDivisons = _division.toArray(new Integer[0]);
		this.lexicalTags = _tag.toArray(new EnumLexicalUnit[0]);
		
		_division = null;
		_tag = null;
	}
	
	protected void syntaxError(char c, int index)
			throws Exception{
		throw new Exception(String.format("Excepted character %s at index %d",
				c == 0? "<EOF>" : Character.toString(c), index));
	}
	
	protected boolean beginOfIdentifier(char c) {
		return between(c, 'a', 'z') || between(c, 'A', 'Z')
				|| inside(c, "_$");
	}
	
	protected boolean between(char c, char a, char b) {
		return a <= c && c <= b;
	}
	
	protected boolean inside(char c, String chars) {
		for(char ch : chars.toCharArray())
			if(ch == c) return true;
		return false;
	}
	
	protected void ack(int index, EnumLexicalUnit unit) {
		_division.add(index);
		_tag.add(unit);
	}
	
	protected void ackOperator(int index, char op) {
		if(op == '(') this.ack(index, EnumLexicalUnit.LEFT_PARENTHESIS);
		else if(op == ')') this.ack(index, EnumLexicalUnit.RIGHT_PARENTHESIS);
		else if(op == ',') this.ack(index, EnumLexicalUnit.COMMA);
		else if(op == ';') this.ack(index, EnumLexicalUnit.SEMICOLON);
		else if(op == '{') this.ack(index, EnumLexicalUnit.LEFT_BRACE);
		else if(op == '}') this.ack(index, EnumLexicalUnit.RIGHT_BRACE);
		else this.ack(index, EnumLexicalUnit.OPERATOR);
	}
	
	public Integer[] getLexicalDivisions() {
		return lexicalDivisons;
	}
	
	public EnumLexicalUnit[] getLexicalTags() {
		return lexicalTags;
	}
	
	public String getSourceCode() {
		return source;
	}
}
