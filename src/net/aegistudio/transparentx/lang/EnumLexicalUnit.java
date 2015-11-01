package net.aegistudio.transparentx.lang;

public enum EnumLexicalUnit {
	OPERATOR(true),
	
	// THESE UNITS ARE SEPERATED FROM OPERATOR
	// TO SIMPLIFY GRAMMATIC ANALYSIS.
	LEFT_PARENTHESIS(true), RIGHT_PARENTHESIS(true),
	LEFT_BRACE(true), RIGHT_BRACE(true),
	COMMA(true),
	SEMICOLON(true),
	
	INTEGER(false),
	FLOAT(false),
	IDENTIFIER(false),
	COMMENT(false),
	BLANK(false);
	
	private final boolean isOperator;
	
	private EnumLexicalUnit(boolean isOperator) {
		this.isOperator = isOperator;
	}
	
	public boolean isOperator(){
		return this.isOperator;
	}
}
