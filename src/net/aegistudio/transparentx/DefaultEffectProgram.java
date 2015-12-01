package net.aegistudio.transparentx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.lwjgl.opengl.ARBShaderObjects;

import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparent.shader.ShaderException;
import net.aegistudio.transparent.shader.ShaderObject;
import net.aegistudio.transparent.shader.ShaderProgram;
import net.aegistudio.transparentx.combine.Combine;
import net.aegistudio.transparentx.lang.SharingVariable;
import net.aegistudio.transparentx.lang.EnumModifier;
import net.aegistudio.transparentx.lang.ShaderExtractor;
import net.aegistudio.transparentx.lang.ShaderLexicalizer;
import net.aegistudio.transparentx.lang.ShaderPreprocessor;
import net.aegistudio.transparentx.lang.ShaderSymbolizer;
import net.aegistudio.transparentx.lang.ShaderTracer;
import net.aegistudio.transparentx.lang.Symbol;

public class DefaultEffectProgram extends ShaderProgram implements ShaderEffectProgram {
	public DefaultEffectProgram() {
		super();
		adaptedShaders = new TreeSet<String>();
		objectCode = new ArrayList<ShaderObject>();
		
		grossGlobalSymbol = new TreeMap<EnumShaderType, Map<String, Symbol>>();
		grossSourceCode = new TreeMap<EnumShaderType, Map<Double, List<String>>>();
		alteringVariable = new TreeSet<String>();
		grossObject = new TreeMap<EnumShaderType, ShaderObject>();
	}
	
	/** Stores shaders that're adapted. **/
	private final Set<String> adaptedShaders;
	
	/** Stores sub-shaders shader objects. Used for attaching.*/
	private final List<ShaderObject> objectCode;
	
	/** Stores global variables (uniform, attribute, etc.) for passing in. */
	private final Map<EnumShaderType, Map<String, Symbol>> grossGlobalSymbol;
	
	/** Stores code segments for calling a sub-shader.*/
	private final Map<EnumShaderType, Map<Double, List<String>>> grossSourceCode;
	
	/** Stores variables that're being overwritten. */
	private final Set<String> alteringVariable;
	
	/** Stores the main code for each object. */
	private final Map<EnumShaderType, ShaderObject> grossObject;
	
	/**
	 * The create process only allocate address for the shader program.
	 */
	public void create() {
		super.allocate();
	}
	
	/**
	 * This method should only be called under context.
	 * @param sfx the shader effect.
	 * @throws Exception if some error encounters.
	 */
	
	public boolean adapt(ShaderEffect sfx) throws Exception {
		String prefix = sfx.getClass().getName().replaceAll("[\\.$]", "_");
		// If target shader already compiled, do nothing to it.
		if(adaptedShaders.contains(prefix)) return false;
		
		//Initialize adaption.
		ShaderEffectClass fxClass = sfx.getShaderEffectClass();
		
		ArrayList<ShaderObject> shaderObjects = new ArrayList<ShaderObject>();
		Map<EnumShaderType, Map<String, Symbol>> globalSymbols 
			= new TreeMap<EnumShaderType, Map<String, Symbol>>();
		Map<EnumShaderType, Set<String>> alteringVariables
			= new TreeMap<EnumShaderType, Set<String>>();
		Map<EnumShaderType, String> codeSegments
			= new TreeMap<EnumShaderType, String>();
		
		// Else firstly get all codes.
		for(EnumShaderType shaderType : EnumShaderType.values()){
			String[] sourceCode = sfx.getRenderSource(shaderType);
			if(sourceCode == null || sourceCode.length == 0)
				continue;	// No such shader, skipping
			
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
			Map<String, Symbol> globalIdentifier = new TreeMap<String, Symbol>();
			Set<String> mutatedVariables = new TreeSet<String>();
			
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
				Combine combine = fxClass.getCombine(varname);
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
					Combine combine = fxClass.getCombine(varname);
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
			
			globalSymbols.put(shaderType, globalIdentifier);
			alteringVariables.put(shaderType, mutatedVariables);
			
			// Make code segment.
			StringBuilder codeSegment = new StringBuilder(String.format("if(enabled_%s) {", prefix));
			codeSegment.append(new String(preprocess));
			codeSegment.append(String.format("%s_main();", prefix));
			codeSegment.append(new String(postprocess));
			codeSegment.append("}");
			codeSegments.put(shaderType, new String(codeSegment));
		}
		
		//Then verify and submit alternations.
		objectCode.addAll(shaderObjects);
		adaptedShaders.add(prefix);
		for(EnumShaderType shaderType : EnumShaderType.values()) {
			if(codeSegments.get(shaderType) == null) continue;
			
			Map<Double, List<String>> codeSegmentMap = grossSourceCode.get(shaderType);
			// When a specific code segment map is null, it means no such type of shader.
			if(codeSegmentMap == null) grossSourceCode.put(shaderType,
					codeSegmentMap = new TreeMap<Double, List<String>>());
			
			List<String> codeSegmentList = codeSegmentMap.get(fxClass.getPriority());
			if(codeSegmentList == null) codeSegmentMap.put(fxClass.getPriority(),
					codeSegmentList = new ArrayList<String>());
			
			codeSegmentList.add(codeSegments.get(shaderType));
			
			Map<String, Symbol> grossGlobalSymbols = this.grossGlobalSymbol.get(shaderType);
			if(grossGlobalSymbols == null) this.grossGlobalSymbol.put(shaderType,
					grossGlobalSymbols = new TreeMap<String, Symbol>());
			grossGlobalSymbols.putAll(globalSymbols.get(shaderType));
			
			this.alteringVariable.addAll(alteringVariables.get(shaderType));
		}
		
		incrementAttach(shaderObjects);
		return true;
	}
	
	protected void incrementAttach(List<ShaderObject> newObjects) throws ShaderException {
		/** detach gross objects **/
		for(ShaderObject shaderObject : grossObject.values()) 
			if(shaderObject != null) {
				super.detach(shaderObject);
				shaderObject.destroy();
			}
		grossObject.clear();
		
		for(ShaderObject newObject : newObjects) 
			super.attach(newObject);
	}
	
	public void recompile() throws ShaderException {
		for(EnumShaderType shaderType : EnumShaderType.values()) {
			String mainCode = constructMainCode(shaderType);
			if(mainCode == null) continue;
			ShaderObject mainObject = new ShaderObject(shaderType, mainCode);
			mainObject.create();
			attach(mainObject);
			grossObject.put(shaderType, mainObject);
		}

		super.assemble(new ShaderObject[0]);
	}
	
	protected void addGlobalVariable(Map<String, Symbol> globals, Set<String> interfaces, String prefix, Symbol identifier) {
		if(identifier.scope != null) return;
		if(identifier.modifier.builtin) return;
		if(identifier.modifier.function) return;
		
		if(identifier.name.charAt(0) == '_') {
			globals.put(identifier.name, identifier);
			interfaces.add(identifier.name);
		}
		else globals.put(String.format("%s_%s", prefix, identifier.name), identifier);
	}
	
	public void destroy() {
		ARBShaderObjects.glUseProgramObjectARB(0);
		for(ShaderObject object : objectCode) {
			super.detach(object);
			object.destroy();
		}
		objectCode.clear();
		adaptedShaders.clear();
		
		for(ShaderObject shaderObject : grossObject.values()) {
			super.detach(shaderObject);
			shaderObject.destroy();
		}
		grossSourceCode.clear();
		grossGlobalSymbol.clear();
		grossObject.clear();
		alteringVariable.clear();
		
		super.destroy();
	}

	/**
	 * Return the main source code for specific shader type.
	 * This method is remained for debugging.
	 * @param shaderType target shader
	 * @return
	 */
	
	public String constructMainCode(EnumShaderType shaderType) {
		Map<Double, List<String>> targetCode = grossSourceCode.get(shaderType);
		if(targetCode == null) return null;
		
		StringBuilder mainCodeConstructor = new StringBuilder();
		
		// -- ENABLED BLOCK AND FUNCTION DECLARATION BLOCK.
		for(String adaptedShader : adaptedShaders) {
			mainCodeConstructor.append("uniform bool enabled_");
			mainCodeConstructor.append(adaptedShader);
			mainCodeConstructor.append(";");
			mainCodeConstructor.append("void ");
			mainCodeConstructor.append(adaptedShader);
			mainCodeConstructor.append("_main();");
		}
		
		// -- GLOBAL BLOCK
		for(Map.Entry<String, Symbol> symbolEntry : 
			grossGlobalSymbol.get(shaderType).entrySet()) {
			mainCodeConstructor.append(symbolEntry.getValue()
					.toDefinition(symbolEntry.getKey()));
			mainCodeConstructor.append(';');
		}
		
		// -- BEGIN OF MAIN
		mainCodeConstructor.append("void main(){");
		
		// -- NORMAL PROCESS BLOCK
		for(String alter : alteringVariable) {
			SharingVariable alteredVar = SharingVariable.getVariable(alter);
			if(alteredVar == null) continue;
			String process = alteredVar.getNormalProcess(shaderType);
			if(process != null) mainCodeConstructor.append(process);
		}
		
		// -- SUB_SHADER CALL BLOCK
		Double[] priorities = targetCode
				.keySet().toArray(new Double[0]);
		Arrays.sort(priorities);
		for(Double entry : priorities)
			for(String sourceCode : targetCode.get(entry))
				mainCodeConstructor.append(sourceCode);
		
		// -- END OF MAIN
		mainCodeConstructor.append('}');
		
		return new String(mainCodeConstructor);
	}

	@Override
	public void push() {
		this.pushShaderProgram();
	}

	@Override
	public void pop() {
		this.popShaderProgram();
	}
}
