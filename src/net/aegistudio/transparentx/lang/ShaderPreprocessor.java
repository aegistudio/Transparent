package net.aegistudio.transparentx.lang;

import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * The task of shader preprocessor is to extract actual code
 * and replace global identifier (not local identifiers).
 * @author aegistudio
 */

public class ShaderPreprocessor {
	
	private final ShaderSymbolizer symbolizer;
	private final ShaderExtractor extractor;
	
	public ShaderPreprocessor(ShaderSymbolizer symbolizer, ShaderExtractor extractor) {
		this.symbolizer = symbolizer;
		this.extractor = extractor;
	}
	/**
	 * The preprocess will do following things:
	 * 1. remove all comment and spaces from the source.
	 * 2. replace all global identifiers with prefixed form.
	 * 3. remove all alternations to some identifiers.
	 * 			(remove write, not read)
	 * 
	 * To simplify, we assume only GL builtins could be contained
	 * in alternations, and which means there's no intersection
	 * between alternation removals and prefixing process.
	 * 
	 * @param prefix the prefix to add, will add to all global
	 * variables and functions that are not built-in.
	 * @param alternationRemovals <identifier, n> will remove n 
	 * alternations from the top of the specific identifier,
	 *  when n > 0, and remove all when n < 0.
	 *
	 */
	String processedSource;
	
	static final int IDLE = 0;
	static final int RECOGNIZED = 1;
	static final int OPERATED = 2;
	static final int EQUAL = 3;
	static final int DOT = 4;
	
	StringBuilder recognizedBuilder;
	String recognizedAlter;
	
	Stack<StringBuilder> builderStack;
	Stack<String> alterStack;
	int state, currentStacked;
	Stack<Integer> stackedRecords;
	Map<String, Integer> alternationRemovals;
	StringBuilder builder;
	
	public void preprocess(String prefix, Map<String, Integer> alternationRemovals) throws Exception{
		if(alternationRemovals == null) this.alternationRemovals = new TreeMap<String, Integer>();
		else this.alternationRemovals = alternationRemovals;
		
		Set<String> mutatingModifier = new TreeSet<String>();
		
		for(Symbol symbol : symbolizer.getSymbols())
			if(symbol.scope == null 
				&& symbol.modifier != EnumModifier.BUILTIN_FUNCTION
				&& symbol.modifier != EnumModifier.BUILTIN_VARIABLE
				// We regard variables begin with '_' as shared variable, which means no aliasing 
				// when these variables occur.
				&& !(symbol.name.charAt(0) == '_'))
					mutatingModifier.add(symbol.name);
		
		String sourceCode = extractor.getExtractedCode();
		Integer[] divisions = extractor.getExtractedDivisions();
		EnumLexicalUnit[] tags = extractor.getExtractedTags();
		
		builder = new StringBuilder();
		int leftIndex = 0;
		
		recognizedBuilder = null;
		recognizedAlter = null;
		
		builderStack = new Stack<StringBuilder>();
		alterStack = new Stack<String>();
		stackedRecords = new Stack<Integer>();
		
		state = IDLE;
		currentStacked = 0;
		
		for(int i = 0; i < divisions.length; i ++) {
			int rightIndex = divisions[i] + 1;
			String current = sourceCode.substring(leftIndex, rightIndex);
			
			//System.out.printf("%d %d %s\n", currentStacked, state, current);
			
			if(tags[i] == EnumLexicalUnit.IDENTIFIER){
				if(mutatingModifier.contains(current)) {
					builder.append(prefix);
					builder.append('_');
				}
				else 
					if(state == IDLE) {
						if(alternationRemovals.containsKey(current)) {
							if(alternationRemovals.get(current) != 0) {
								state = RECOGNIZED;
								recognizedAlter = current;
								recognizedBuilder = new StringBuilder(current);
								leftIndex = rightIndex;
								continue;
							}
						}
					}
			}
			
			if(state == RECOGNIZED && tags[i] == EnumLexicalUnit.OPERATOR) {
				if("[".equals(current)){
					builderStack.push(builder);
					alterStack.push(recognizedAlter);
					builder = recognizedBuilder;
					recognizedBuilder = null;
					recognizedAlter = null;
					state = IDLE;
				}
			}
			
			if(state == IDLE) {
				switch(tags[i]) {
					case LEFT_PARENTHESIS:
					case LEFT_BRACE:
						stackedRecords.push(currentStacked);
						currentStacked = 0;
					break;
					
					case RIGHT_PARENTHESIS:
					case RIGHT_BRACE:
						doStack();
						currentStacked = stackedRecords.pop();
					break;
				
					case SEMICOLON:
					case COMMA:
						doStack();
					break;
					
					default:
					break;
				}
				
				builder.append(current);
				if(tags[i] == EnumLexicalUnit.OPERATOR && "]".equals(current)) {
					if(!builderStack.isEmpty()) {
						recognizedBuilder = builder;
						builder = builderStack.pop();
						recognizedAlter = alterStack.pop();
						state = RECOGNIZED;
					}
				}
			}
			else { 
				switch(tags[i]){
					case IDENTIFIER:
						if(state == DOT) {
							recognizedBuilder.append('.');
							state = RECOGNIZED;
						}
						else if(state == EQUAL) {
							builder.append(current);
							ackRemoval();
						}
						else if(state == OPERATED) 
							abort(current);
						else syntaxError();
					break;
					
					case OPERATOR:
						if(state == RECOGNIZED){
							if(".".equals(current)) {
								//DEREFERENCING.
								recognizedBuilder.append('.');
								state = DOT;
							}
							else if("=".equals(current)) {
								//ONE EQUAL
								state = EQUAL;
							}
							else if("+-*/&|^".contains(current)) {
								//AFTER A OPERATOR.
								state = OPERATED;
								recognizedBuilder.append(')');
								recognizedBuilder.append(current);
							}
						}
						else if(state == EQUAL) {
							if("=".equals(current)) {
								//MEANS DOUBLE EQUAL, LOGIC OPERATOR.
								state = IDLE;
								builder.append(recognizedBuilder);
								builder.append("==");
							}
							else {
								ackRemoval();
							}
						}
						else if(state == OPERATED) {
							if("=".equals(current)) {
								this.ackStackedRemoval();
							}
							else abort(current);
						}
					break;
					
					default:
						if(state == EQUAL) {
							builder.append(current);
							ackRemoval();
						}
						else {
							abort(current);
						}
					break;
				}
			}
			
			leftIndex = rightIndex;
		}
		
		this.processedSource = new String(builder);
	}
	
	protected void syntaxError() throws Exception {
		
	}
	
	protected void abort(String current) {
		if(state == OPERATED) builder.append('(');
		state = IDLE;
		builder.append(recognizedBuilder);
		builder.append(current);
		recognizedBuilder = null;
		recognizedAlter = null;
	}
	
	protected void ackStackedRemoval() {
		builder.append('(');
		builder.append(recognizedBuilder);
		builder.append('(');
		currentStacked ++;
		this.ackRemoval();
	}
	
	protected void doStack() {
		for(int i = 0; i < currentStacked; i ++)
			builder.append(')');
		currentStacked = 0;
	}
	
	protected void ackRemoval() {
		state = IDLE;
		alternationRemovals.replace(recognizedAlter, 
				alternationRemovals.get(recognizedAlter) - 1);
		recognizedAlter = null;
		recognizedBuilder = null;
	}
	
	public String getProcessedSource() {
		return this.processedSource;
	}
	
}
