package net.aegistudio.transparentx.lang;

import java.util.Stack;
import java.util.TreeSet;

/**
 * Shader tracer traces what variables are being updated in
 * the code. The result will be used to ask shader effect
 * how to handle these variables.
 * 
 * @author aegistudio
 */

public class ShaderTracer {
	private final ShaderExtractor extractor;
	private final ShaderSymbolizer symbolizer;
	public ShaderTracer(ShaderExtractor extractor, ShaderSymbolizer symbolizer) {
		this.symbolizer = symbolizer;
		this.extractor = extractor;
	}
	
	static final int IDLE = 0;
	static final int RECOGNIZED = 1;
	static final int EQUAL = 2;
	static final int OPERATED = 3;
	static final int DOT = 4;
	
	TreeSet<String> intended;
	TreeSet<String> ensured;
	int state;
	String recognized;
	
	public void trace() {
		String sourceCode = extractor.getExtractedCode();
		intended = new TreeSet<String>();
		ensured = new TreeSet<String>();
		
		for(Symbol symbol : symbolizer.getSymbols()) 
			if(symbol.modifier.variable)
				if(symbol.modifier.builtin || symbol.name.charAt(0) == '_')
					intended.add(symbol.name);
		
		Integer[] divisions = this.extractor.getExtractedDivisions();
		EnumLexicalUnit[] tags = this.extractor.getExtractedTags();
		
		int leftIndex = 0;
		state = IDLE;
		
		Stack<String> recognizedStacks = new Stack<String>();
		recognized = null;
		
		for(int i = 0; i < divisions.length; i ++) {
			int rightIndex = divisions[i] + 1;
			String current = sourceCode.substring(leftIndex, rightIndex);
			
			switch(tags[i]) {
				case IDENTIFIER:
					if(state == EQUAL) accept();
					else if(state == OPERATED) reject();
					else if(state == DOT) {
						state = RECOGNIZED;
					}
					
					if(state == IDLE) {
						if(intended.contains(current)) {
							state = RECOGNIZED;
							recognized = current;
						}
					}
				break;
				
				case OPERATOR:
					if("]".equals(current)) {
						if(!recognizedStacks.isEmpty()) {
							recognized = recognizedStacks.pop();
							state = RECOGNIZED;
						}
					}
					else if(state == RECOGNIZED){
						if("[".equals(current)) {
							if(recognized != null){
								recognizedStacks.push(recognized);
								state = IDLE;
							}
						}
						else if("+-*/&|^".contains(current)){
							state = OPERATED;
						}
						else if("=".equals(current)) {
							state = EQUAL;
						}
						else if(".".equals(current)) {
							state = DOT;
						}
					}
					else if(state == EQUAL) {
						if("=".equals(current)) 
							reject();
						else accept();
					}
					else if(state == OPERATED) {
						if("=".equals(current))
							accept();
						else reject();
					}
					else if(state == DOT) reject();
				break;
				
				default:
					if(state == EQUAL) 
						accept();
					else if(state == RECOGNIZED || state == OPERATED) 
						reject();
					else if(state == DOT) reject();
				break;
			}
			
			leftIndex = rightIndex;
		}
	}
	
	protected void accept() {
		state = IDLE;
		intended.remove(recognized);
		ensured.add(recognized);
		recognized = null;
	}
	
	protected void reject() {
		state = IDLE;
		recognized = null;
	}
	
	public String[] getAlteredVariable() {
		return ensured.toArray(new String[0]);
	}
}
