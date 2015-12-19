package net.aegistudio.transparentx.prog;

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
import net.aegistudio.transparentx.ShaderEffect;
import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.ShaderEffectProgram;
import net.aegistudio.transparentx.lang.SharingVariable;
import net.aegistudio.transparentx.lang.Symbol;

/**
 * Abstract Effect Program provides a common way for a shader effect program to build itself.<br>
 * If you want to try something different, please implement ShaderEffectProgram interface.<br>
 * <br><br>
 * 
 * A controversial shader effect program contains 4 parts in its main code:<br><br>
 * &lt;globalVariableBlock&gt;<br>
 * <br>
 * void main() { <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;normalProcessBlock&gt;<br>
 * <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;callbackBlock&gt;<br>
 * <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;postmortenBlock&gt;<br>
 * }<br><br>
 * 
 * <b>globalVariableBlock</b>: in this block, we declare global variables in order to accept user input
 * like uniform and attribute. Some varying declaration and inside-the-program variables will also
 * be declared here. <br><br>
 * 
 * <b>normalProcessBlock</b>: in this block, some OpenGL-builtin variables and some shared variables will
 * be initialized here. (Like gl_Position = ftransform();) <br><br>
 * 
 * <b>callbackBlock</b>: in this block, we call the 'main' of subprograms to actually make it run.<br><br>
 * 
 * <b>postmortenBlock</b>: we will do postmorten job in this block. Sometimes we need to write to gl_FragData
 * and so on, and this will be useful.<br><br>
 * 
 * @author aegistudio
 */

public abstract class AbstractEffectProgram extends ShaderProgram implements ShaderEffectProgram {
	protected abstract SubprogramWorker newSubprogramWorker(EnumShaderType shaderType, ShaderEffectClass sfxClazz, String prefix, String[] sourceCode);
	
	/** Scoping Helpers **/
	/** Stores the main code for each object. */
	protected final Set<String> adaptedShaders;
	protected final Map<EnumShaderType, ShaderObject> grossObject;
	protected final Map<EnumShaderType, Map<Double, List<SubprogramWorker>>> subprogramWorker;
	protected abstract Map<Double, List<SubprogramWorker>> createSubprogramWorkerMap();
	protected abstract List<SubprogramWorker> createSubprogramWorkerList();
	
	protected AbstractEffectProgram(Set<String> adaptedShaders, 
			Map<EnumShaderType, ShaderObject> grossObject, 
			Map<EnumShaderType, Map<Double, List<SubprogramWorker>>> subprogramWorker) {
		this.adaptedShaders = adaptedShaders;
		this.grossObject = grossObject;
		this.subprogramWorker = subprogramWorker;
	}
	
	@Override
	public boolean adapt(ShaderEffect sfx) throws Exception {
		/** Decide whether to accept shader object **/
		if(sfx == null) return false;
		String prefix = sfx.getClass().getName().replaceAll("[\\.$]", "_");
		if(adaptedShaders.contains(prefix)) return false;
		
		/** Compilation of subprograms. **/
		TreeMap<EnumShaderType, SubprogramWorker> workers = new TreeMap<EnumShaderType, SubprogramWorker>();
		ShaderEffectClass sfxClazz = sfx.getShaderEffectClass();
		for(EnumShaderType shaderType : EnumShaderType.values()) {
			String[] sourceCode = sfx.getRenderSource(shaderType);
			if(sourceCode == null || sourceCode.length == 0) continue;

			SubprogramWorker worker = this.newSubprogramWorker(shaderType, sfxClazz, prefix, sourceCode);
			worker.compile();
			workers.put(shaderType, worker);
		}
		adaptedShaders.add(prefix);
		
		/** Clean up former generated main code. **/
		for(ShaderObject shaderObject : grossObject.values()) 
			if(shaderObject != null) {
				super.detach(shaderObject);
				shaderObject.destroy();
			}
		grossObject.clear();
		
		/** Increment attach worker **/
		for(EnumShaderType shaderType : EnumShaderType.values()) {
			SubprogramWorker worker = workers.get(shaderType);
			if(worker == null) continue;
			for(ShaderObject object : worker.object()) super.attach(object);
			
			Map<Double, List<SubprogramWorker>> shaderTypeMap = this.subprogramWorker.get(shaderType);
			if(shaderTypeMap == null) this.subprogramWorker.put(shaderType, shaderTypeMap = this.createSubprogramWorkerMap());
			double priority = sfxClazz.getPriority();
			List<SubprogramWorker> subprogramWorkerList = shaderTypeMap.get(priority);
			if(subprogramWorkerList == null) shaderTypeMap.put(priority, subprogramWorkerList = this.createSubprogramWorkerList());
			subprogramWorkerList.add(worker);
		}
		return true;
	}
	
	@Override
	public void recompile() throws ShaderException {
		/** Construct the main code, then compile and attach it.**/
		for(EnumShaderType shaderType : EnumShaderType.values()) {
			String mainCode = constructMainCode(shaderType, this.subprogramWorker.get(shaderType));
			if(mainCode == null || mainCode.length() == 0) continue;
			ShaderObject mainObject = new ShaderObject(shaderType, mainCode);
			mainObject.create();
			attach(mainObject);
			grossObject.put(shaderType, mainObject);
		}
		
		/** Assemble and validate the program **/
		super.assemble(new ShaderObject[0]);
	}

	protected String constructMainCode(EnumShaderType shaderType, Map<Double, List<SubprogramWorker>> subprogramMap) {
		if(subprogramMap == null || subprogramMap.size() == 0) return null;
		
		StringBuilder mainCodeConstructor = new StringBuilder();
		
		/** <globalVariableBlock> **/
		// Subprogram enable flags and main functions.
		for(String adaptedShader : adaptedShaders) {
			mainCodeConstructor.append("uniform bool enabled_");
			mainCodeConstructor.append(adaptedShader);
			mainCodeConstructor.append(";");
			mainCodeConstructor.append("void ");
			mainCodeConstructor.append(adaptedShader);
			mainCodeConstructor.append("_main();");
		}
		
		// Concrete global variables.
		TreeMap<String, Symbol> grossGlobalSymbol = new TreeMap<String, Symbol>();
		for(List<SubprogramWorker> workers : subprogramMap.values()) 
			for(SubprogramWorker worker : workers)
				grossGlobalSymbol.putAll(worker.globalVariables());
		
		for(Map.Entry<String, Symbol> symbolEntry : 
			grossGlobalSymbol.entrySet()) {
			mainCodeConstructor.append(symbolEntry.getValue()
					.toDefinition(symbolEntry.getKey()));
			mainCodeConstructor.append(';');
		}
		
		// Begin of main()
		mainCodeConstructor.append("void main(){");
		
		/** <normalPreprocessBlock> **/
		TreeSet<String> alteringVariable = new TreeSet<String>();
		for(List<SubprogramWorker> workers : subprogramMap.values()) 
			for(SubprogramWorker worker : workers)
				alteringVariable.addAll(worker.normalProcessVariable());
		
		for(String alter : alteringVariable) {
			SharingVariable alteredVar = SharingVariable.getVariable(alter);
			if(alteredVar == null) continue;
			String process = alteredVar.getNormalProcess(shaderType);
			if(process != null) mainCodeConstructor.append(process);
		}
		
		/** <callbackBlock> **/
		Double[] priorities = subprogramMap.keySet().toArray(new Double[0]);
		Arrays.sort(priorities);
		for(Double entry : priorities)
			for(SubprogramWorker program : subprogramMap.get(entry))
				mainCodeConstructor.append(program.callbackBlock());
		
		// End of main()
		mainCodeConstructor.append('}');
		return new String(mainCodeConstructor);
	}
	
	public void create() {
		super.allocate();
	}

	@Override
	public void push() {
		super.pushShaderProgram();
	}

	@Override
	public void pop() {
		super.popShaderProgram();
	}
	
	public void destroy() {
		ARBShaderObjects.glUseProgramObjectARB(0);
		for(Map<Double, List<SubprogramWorker>> subprogramMap : this.subprogramWorker.values())
			for(List<SubprogramWorker> workers : subprogramMap.values()) 
				for(SubprogramWorker worker : workers) 
					for(ShaderObject object : worker.object()) {
						super.detach(object);
						object.destroy();
					}
		subprogramWorker.clear();
		adaptedShaders.clear();
		
		for(ShaderObject shaderObject : grossObject.values()) {
			super.detach(shaderObject);
			shaderObject.destroy();
		}
		grossObject.clear();
		
		super.destroy();
	}
}
