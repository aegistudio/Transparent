package net.aegistudio.transparentx.lang;

import java.util.TreeMap;

/**
 * This class build symbol table for a shader based on
 * the result of extracting.
 * 
 * The symbol table will consists of the following message:
 * 1. identifier name
 * 2. identifier type
 * 3. identifier modifier
 * 4. identifier scope (global or local)
 * 
 * @author aegistudio
 */

public class ShaderSymbolizer {
	ShaderExtractor extractor;
	
	public ShaderSymbolizer(ShaderExtractor extractor) {
		this.extractor = extractor;
	}
	
	TreeMap<String, Symbol> symbols;
	
	static final int GLOBAL = 0;
	static final int AFTER_GLOBAL_MODIFIER = 1;
	static final int AFTER_GLOBAL_TYPE = 2;
	static final int GLOBAL_SYMBOL = 3;
	static final int GLOBAL_ARRAY = 4;
	
	static final int PARAMETER_LIST = 5;
	static final int PARAMETER_LIST_MODIFIER = 6;
	static final int PARAMETER_LIST_TYPE = 7;
	static final int PARAMETER_LIST_SYMBOL = 8;
	static final int PARAMETER_LIST_END = 9;
	
	static final int LOCAL = 10;
	static final int LOCAL_AFTER_TYPE = 11;
	
	EnumModifier modifier = null;
	String type = null;
	String name = null;
	String scope = null;
	StringBuilder arrayBuilder = null;
	
	public void symbolize() throws Exception{
		Integer[] divisions = extractor.getExtractedDivisions();
		EnumLexicalUnit[] tags = extractor.getExtractedTags();
		String source = extractor.getExtractedCode();
		
		symbols = new TreeMap<String, Symbol>();
		
		int state = GLOBAL;
		int leftIndex = 0;
		int braceStack = 0;
		
		modifier = null;
		type = null;
		name = null;
		scope = null;
		arrayBuilder = null;
		
		for(int i = 0; i < divisions.length; i ++) {
			int rightIndex = divisions[i] + 1;
			EnumLexicalUnit tag = tags[i];
			String current = source.substring(leftIndex, rightIndex);
			
			switch(state) {
				case GLOBAL:
					if(tag == EnumLexicalUnit.IDENTIFIER) {
						EnumKeyword keyword = EnumKeyword.getKeyword(current);
						if(keyword == EnumKeyword.MODIFIER){
							this.changeModifier(current);
							state = AFTER_GLOBAL_MODIFIER;
						}
						else if(keyword == EnumKeyword.TYPE) {
							modifier = EnumModifier.NONE;
							type = current;
							state = AFTER_GLOBAL_TYPE;
						}
						else this.syntaxError(current);
					}
				break;
				
				case AFTER_GLOBAL_MODIFIER:
					if(tag == EnumLexicalUnit.IDENTIFIER) {
						EnumKeyword keyword = EnumKeyword.getKeyword(current);
						if(keyword == EnumKeyword.TYPE) {
							type = current;
							state = AFTER_GLOBAL_TYPE;
						}
					}
				break;
				
				case AFTER_GLOBAL_TYPE:
					if(tag == EnumLexicalUnit.IDENTIFIER) {
						if(EnumKeyword.getKeyword(current) == null) {
							name = current;
							state = GLOBAL_SYMBOL;
						}
						else syntaxError(current);
					}
				break;
				
				case GLOBAL_SYMBOL:
					if(tag == EnumLexicalUnit.LEFT_PARENTHESIS) {
						if(modifier != EnumModifier.NONE) 
							this.syntaxError(modifier.name());
						
						modifier = EnumModifier.FUNCTION;
						if("main".equals(name)) 
							modifier = EnumModifier.MAIN;
						
						this.makeSymbol();
						scope = name;
						state = PARAMETER_LIST;
						
						type = null;
						modifier = null;
					}
					else if(tag == EnumLexicalUnit.OPERATOR || tag == EnumLexicalUnit.SEMICOLON) {
						if(current.equals("[")) {
							state = GLOBAL_ARRAY;
							arrayBuilder = new StringBuilder();
							braceStack = 0;
						}
						else 
						{
							this.makeSymbol();
							state = GLOBAL;
							if(tag == EnumLexicalUnit.SEMICOLON) {
								type = null;
								modifier = null;
							}
						}
					}
					else if(tag == EnumLexicalUnit.COMMA) {
						this.makeSymbol();
						name = null;
						arrayBuilder = null;
						state = AFTER_GLOBAL_TYPE;
					}
				break;
				
				case GLOBAL_ARRAY:
					if(current.equals("]"))
						if(braceStack == 0) {
							state = GLOBAL_SYMBOL;
							break;
						}
					arrayBuilder.append(current);
					
					if(tag == EnumLexicalUnit.LEFT_PARENTHESIS || tag == EnumLexicalUnit.LEFT_BRACE
						|| (tag == EnumLexicalUnit.OPERATOR && current.equals("[")))
						braceStack ++;
					else if(tag == EnumLexicalUnit.RIGHT_PARENTHESIS || tag == EnumLexicalUnit.RIGHT_BRACE
							|| (tag == EnumLexicalUnit.OPERATOR && current.equals("]")))
						braceStack --;
				break;
				
				case PARAMETER_LIST:
					if(tag == EnumLexicalUnit.IDENTIFIER) {
						EnumKeyword keyword = EnumKeyword.getKeyword(current);
						if(keyword == EnumKeyword.MODIFIER) {
							this.changeModifier(current);
							state = PARAMETER_LIST_MODIFIER;
						}
						else if(keyword == EnumKeyword.TYPE) {
							modifier = EnumModifier.NONE;
							type = current;
							state = PARAMETER_LIST_TYPE;
						}
					}
					else if(tag == EnumLexicalUnit.RIGHT_PARENTHESIS) {
						state = PARAMETER_LIST_END;
					}
				break;
				
				case PARAMETER_LIST_MODIFIER:
					if(tag == EnumLexicalUnit.IDENTIFIER) {
						EnumKeyword keyword = EnumKeyword.getKeyword(current);
						if(keyword == EnumKeyword.TYPE) {
							type = current;
							state = PARAMETER_LIST_TYPE;
						}
					}
				break;
				
				case PARAMETER_LIST_TYPE:
					if(tag == EnumLexicalUnit.IDENTIFIER) {
						if(EnumKeyword.getKeyword(current) == null) {
							name = current;
							state = PARAMETER_LIST_SYMBOL;
						}
						else syntaxError(current);
					}
				break;
				
				case PARAMETER_LIST_SYMBOL:
					if(tag == EnumLexicalUnit.COMMA) {
						this.makeSymbol();
						state = PARAMETER_LIST;
						type = null;
						modifier = null;
					}
					else if(tag == EnumLexicalUnit.RIGHT_PARENTHESIS) {
						this.makeSymbol();
						state = PARAMETER_LIST_END;
						type = null;
						modifier = null;
					}
				break;
				
				case PARAMETER_LIST_END:
					if(tag == EnumLexicalUnit.SEMICOLON) {
						state = GLOBAL;
						scope = null;
					}
					if(tag == EnumLexicalUnit.LEFT_BRACE) {
						state = LOCAL;
						braceStack = 1;
						modifier = null;
					}
				break;
				
				case LOCAL:
					if(tag == EnumLexicalUnit.IDENTIFIER) {
						EnumKeyword keyword = EnumKeyword.getKeyword(current);
						if(keyword == EnumKeyword.MODIFIER) {
							this.syntaxError(current);
						}
						else if(keyword == EnumKeyword.TYPE) {
							modifier = EnumModifier.NONE;
							type = current;
							state = LOCAL_AFTER_TYPE;
						}
						else if(keyword == EnumKeyword.BUILTIN_VARIABLE) {
							symbols.put(current, new Symbol(current, null,
								EnumModifier.BUILTIN_VARIABLE, null, null));
						}
						if(keyword == EnumKeyword.BUILTIN_FUNCTION) {
							symbols.put(current, new Symbol(current, null,
								EnumModifier.BUILTIN_FUNCTION, null, null));
						}
					}
					else if(tag == EnumLexicalUnit.LEFT_BRACE) {
						braceStack ++;
					}
					else if(tag == EnumLexicalUnit.RIGHT_BRACE) {
						braceStack --;
						if(braceStack == 0) {
							state = GLOBAL;
							scope = null;
						}
					}
				break;
				
				case LOCAL_AFTER_TYPE :
					if(tag == EnumLexicalUnit.LEFT_PARENTHESIS) {
						symbols.put(current, new Symbol(type, null,
								EnumModifier.BUILTIN_FUNCTION, null, null));
						state = LOCAL;
					}
					else if(tag == EnumLexicalUnit.IDENTIFIER) {
						name = current;
						makeSymbol();
						state = LOCAL;
					}
				break;
			}
			
			leftIndex = rightIndex;
		}
	}
	
	protected void changeModifier(String input) {
		if(input == null) return;
		modifier = EnumModifier.valueOf(input.toUpperCase());
	}
	
	protected void makeSymbol() {
		if(!symbols.containsKey(name))
			symbols.put(name, new Symbol(name, type, modifier, scope, 
					arrayBuilder == null? null : new String(arrayBuilder)));
	}
	
	protected void syntaxError(String string) throws Exception{
		throw new Exception(String.format("Unrecognized sequence %s", string));
	}
	
	public Symbol[] getSymbols() {
		return symbols.values().toArray(new Symbol[0]);
	}
}
