package net.aegistudio.transparentx.lang;

import java.util.ArrayList;

/**
 * Extractor will extract useful information from the original
 * source code based on the lexical analysis result.
 * 
 * 1. the comments will be removed after extracting.
 * 2. the blanks between identifiers will be replaced with single ' '.
 * 3. the blanks between different operators will be removed.
 * 
 * @author aegistudio
 */

public class ShaderExtractor {
	private final ShaderLexicalizer lexicalizer;
	
	public ShaderExtractor(ShaderLexicalizer lexicalizer) {
		this.lexicalizer = lexicalizer;
	}
	
	StringBuilder _builder;
	ArrayList<Integer> _divisions;
	ArrayList<EnumLexicalUnit> _tags;
	
	private String extractedSourceCode;
	private Integer[] extractedDivisions;
	private EnumLexicalUnit[] extractedTags;
	
	
	static final int OPERATOR = 0;
	static final int IDENTIFIER = 1;
	static final int IDENTIFIER_BLANK = 2;
	
	int accum = 0;
	
	public void extract() throws Exception {
		Integer[] divisions = lexicalizer.getLexicalDivisions();
		EnumLexicalUnit[] tags = lexicalizer.getLexicalTags();
		String sourceCode = lexicalizer.getSourceCode();
		
		_builder = new StringBuilder();
		_divisions = new ArrayList<Integer>();
		_tags = new ArrayList<EnumLexicalUnit>();
		accum = 0;
		
		int leftIndex = 0;
		int state = OPERATOR;
		
		for(int i = 0; i < divisions.length; i ++) {
			int rightIndex = divisions[i] + 1;
			EnumLexicalUnit type = tags[i];
			String substring = sourceCode.substring(leftIndex, rightIndex);
			
			if(type != EnumLexicalUnit.COMMENT) 
				switch(state) {
					case OPERATOR:
						if(type == EnumLexicalUnit.BLANK) break;
						else if(type.isOperator()){
							if(type == EnumLexicalUnit.RIGHT_PARENTHESIS)
								state = IDENTIFIER;
							ack(substring, type);
						}
						else {
							state = IDENTIFIER;
							ack(substring, type);
						}
					break;
					case IDENTIFIER:
						if(type == EnumLexicalUnit.BLANK) {
							state = IDENTIFIER_BLANK;
						}
						else if(type.isOperator()) {
							state = OPERATOR;
							ack(substring, type);
						}
						else ack(substring, type);
					break;
					case IDENTIFIER_BLANK:
						if(type.isOperator()) {
							if(type == EnumLexicalUnit.RIGHT_PARENTHESIS)
								state = IDENTIFIER;
							else state = OPERATOR;
							ack(substring, type);
						}
						else {
							state = IDENTIFIER;
							ack(" ", EnumLexicalUnit.BLANK);
							ack(substring, type);
						}
					break;
				}
			
			leftIndex = rightIndex;
		}
		
		this.extractedSourceCode = new String(_builder);
		this.extractedDivisions = _divisions.toArray(new Integer[0]);
		this.extractedTags = _tags.toArray(new EnumLexicalUnit[0]);
		
		_builder = null;
		_divisions = null;
		_tags = null;
	}
	
	public void ack(String codeSeg, EnumLexicalUnit tag) {
		_builder.append(codeSeg);
		accum += codeSeg.length();
		_divisions.add(accum - 1);
		_tags.add(tag);
	}
	
	public Integer[] getExtractedDivisions() {
		return extractedDivisions;
	}
	
	public EnumLexicalUnit[] getExtractedTags() {
		return extractedTags;
	}
	
	public String getExtractedCode() {
		return extractedSourceCode;
	}
}
