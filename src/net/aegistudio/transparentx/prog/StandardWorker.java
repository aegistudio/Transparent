package net.aegistudio.transparentx.prog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparent.shader.ShaderObject;
import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.combine.Combine;
import net.aegistudio.transparentx.lang.EnumModifier;
import net.aegistudio.transparentx.lang.ShaderExtractor;
import net.aegistudio.transparentx.lang.ShaderLexicalizer;
import net.aegistudio.transparentx.lang.ShaderPreprocessor;
import net.aegistudio.transparentx.lang.ShaderSymbolizer;
import net.aegistudio.transparentx.lang.ShaderTracer;
import net.aegistudio.transparentx.lang.SharingVariable;
import net.aegistudio.transparentx.lang.Symbol;

public class StandardWorker implements SubprogramWorker {
	private final String[] sourceCode;
	private final String prefix;
	private final ShaderEffectClass sfxClazz;
	private final EnumShaderType shaderType;
	
	public StandardWorker(EnumShaderType shaderType, ShaderEffectClass sfxClazz,
			String prefix, String[] sourceCode) {
		this.sourceCode = sourceCode;
		this.sfxClazz = sfxClazz;
		this.prefix = prefix;
		this.shaderType = shaderType;
	}
	
	private List<ShaderObject> shaderObjects = new ArrayList<ShaderObject>();
	private String codeSegment;
	Map<String, Symbol> globalIdentifier = new TreeMap<String, Symbol>();
	Set<String> mutatedVariables = new TreeSet<String>();
	
	@Override
	public void compile() throws Exception {
		
		// Initialize shader preprocessor components.
		ShaderLexicalizer[] lexicalizers = new ShaderLexicalizer[sourceCode.length];
		ShaderExtractor[] extractors = new ShaderExtractor[sourceCode.length];
		ShaderSymbolizer[] symbolizers = new ShaderSymbolizer[sourceCode.length];
		ShaderTracer[] tracers = new ShaderTracer[sourceCode.length];
		ShaderPreprocessor[] preprocessors = new ShaderPreprocessor[sourceCode.length];
		
		for(int i = 0; i < sourceCode.length; i++) {
			lexicalizers[i] = new ShaderLexicalizer();
			extractors[i] = new ShaderExtractor(lexicalizers[i]);
			symbolizers[i] = new ShaderSymbolizer(extractors[i]);
			
			tracers[i] = new ShaderTracer(extractors[i], symbolizers[i]);
			preprocessors[i] = new ShaderPreprocessor(symbolizers[i], extractors[i]);
		}
		
		// Do preprocessing operations to main shader.
		lexicalizers[0].lexicalize(sourceCode[0]);
		extractors[0].extract();
		symbolizers[0].symbolize();
		
		// Find main function in shader, and add variables to symbol table.
		boolean containsMain = false;
		for(Symbol symbol : symbolizers[0].getSymbols()) 
			if(symbol.modifier == EnumModifier.MAIN) containsMain = true;
			else addGlobalVariable(globalIdentifier, mutatedVariables, prefix, symbol);
		if(!containsMain) throw new Exception("No main function found in shader!");
		
		// Traces altering variables and find their combining policy.
		StringBuilder preprocess = new StringBuilder();
		StringBuilder postprocess = new StringBuilder();
		Map<String, Integer> alternation = new TreeMap<String, Integer>();

		tracers[0].trace();
		
		for(String varname : tracers[0].getAlteredVariable()) {
			SharingVariable variable = SharingVariable.getVariable(varname);
			if(variable == null) continue;
			mutatedVariables.add(varname);
			Combine combine = sfxClazz.getCombine(varname);
			combine.combine(variable.getVariableType(), varname, prefix);
			alternation.put(varname, combine.getReplacement());
			preprocess.append(combine.getPreprocessCode());
			postprocess.append(combine.getPostprocessCode());
		}
		
		// Preprocess main code and form its main object.
		preprocessors[0].preprocess(prefix, alternation);
		ShaderObject main = new ShaderObject(shaderType,
				preprocessors[0].getProcessedSource());
		main.create();
		
		shaderObjects.add(main);
		
		// Do preprocessing to non-main shaders.
		for(int i = 1; i < sourceCode.length; i ++) {
			lexicalizers[i].lexicalize(prefix);
			extractors[i].extract();
			symbolizers[i].symbolize();
			
			// Find main function in shader, and add variables to symbol table.
			for(Symbol symbol : symbolizers[0].getSymbols()) 
				if(symbol.modifier == EnumModifier.MAIN)
					throw new Exception("Duplicate main function in shader!");
				else addGlobalVariable(globalIdentifier, mutatedVariables, prefix, symbol);
			
			// Traces altering variables and find their combining policy.
			tracers[i].trace();
			alternation = new TreeMap<String, Integer>();
			for(String varname : tracers[i].getAlteredVariable()) {
				if(mutatedVariables.contains(varname)) continue;
				SharingVariable variable = SharingVariable.getVariable(varname);
				if(variable == null) continue;
				mutatedVariables.add(varname);
				Combine combine = sfxClazz.getCombine(varname);
				combine.combine(variable.getVariableType(), varname, prefix);
				alternation.put(varname, combine.getReplacement());
				preprocess.append(combine.getPreprocessCode());
				postprocess.append(combine.getPostprocessCode());
			}
			
			preprocessors[i].preprocess(prefix, alternation);
			ShaderObject current = new ShaderObject(shaderType,
					preprocessors[i].getProcessedSource());
			current.create();
			
			shaderObjects.add(current);
		}
		
		// Make code segment.
		StringBuilder codeSegment = new StringBuilder(String.format("if(enabled_%s) {", prefix));
		codeSegment.append(new String(preprocess));
		codeSegment.append(String.format("%s_main();", prefix));
		codeSegment.append(new String(postprocess));
		codeSegment.append("}");
		this.codeSegment = new String(codeSegment);
	}

	protected void addGlobalVariable(Map<String, Symbol> globals, Set<String> interfaces, String prefix, Symbol identifier) {
		if(identifier.scope != null) return;
		if(identifier.modifier.builtin) return;
		if(identifier.modifier.function) return;
		if(identifier.modifier.equals(EnumModifier.CONST)) return;
		
		if(identifier.name.charAt(0) == '_') {
			globals.put(identifier.name, identifier);
			interfaces.add(identifier.name);
		}
		else globals.put(String.format("%s_%s", prefix, identifier.name), identifier);
	}
	
	@Override
	public List<ShaderObject> object() {
		return shaderObjects;
	}

	@Override
	public Map<String, Symbol> globalVariables() {
		return globalIdentifier;
	}

	@Override
	public String callbackBlock() {
		return codeSegment;
	}

	@Override
	public Set<String> normalProcessVariable() {
		return mutatedVariables;
	}
}
