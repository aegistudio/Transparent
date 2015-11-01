package net.aegistudio.transparentx.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.aegistudio.transparent.shader.EnumShaderType;

/**
 * Sharing variables are those who sub-shaders could modify
 * without generating any error. Nearly all read-write 
 * GL-variables could be modify. However, there's some 
 * cases that we need some variables as interfaces. So
 * here comes our idea.
 * 
 * @author aegistudio
 */

public class SharingVariable {
	private final String name, type;
	private final Map<EnumShaderType, String> normalProcessMap;
	
	private final EnumShaderType shaderTarget;
	private final String normalProcess;
	
	public SharingVariable(String name, String type, 
			Map<EnumShaderType, String> normalProcess) {
		this.name = name; this.type = type;
		
		if(normalProcess == null || normalProcess.keySet().size() != 1) {
			this.normalProcessMap = normalProcess;
			this.shaderTarget = null;
			this.normalProcess = null;
		}
		else {
			this.normalProcessMap = null;
			this.shaderTarget = normalProcess.keySet().toArray(new EnumShaderType[0])[0];
			this.normalProcess = normalProcess.get(this.shaderTarget);
		}
	}
	
	public SharingVariable(String name, String type, 
			EnumShaderType shaderTarget, String normalProcess) {
		this.name = name;
		this.type = type;
		this.shaderTarget = shaderTarget;
		this.normalProcess = normalProcess;
		this.normalProcessMap = null;
	}
	
	public void submit() {
		if(!nameTypeMap.containsKey(name))
			nameTypeMap.put(name, this);
	}
	
	private static final TreeMap<String, SharingVariable> nameTypeMap
		= new TreeMap<String, SharingVariable>();
	private static final List<String> builtins = new ArrayList<String>();
	
	static {
		putBuiltin("gl_Vertex", "vec4");
		putBuiltin("gl_Position", "vec4", EnumShaderType.VERTEX, "gl_Position=ftransform();");
		putBuiltin("gl_FrontColor", "vec4", EnumShaderType.VERTEX, "gl_FrontColor=gl_Color;");
		putBuiltin("gl_BackColor", "vec4", EnumShaderType.VERTEX, "gl_BackColor=gl_Color;");
		putBuiltin("gl_FrontSecondaryColor", "vec4", EnumShaderType.VERTEX, "gl_FrontSecondaryColor=gl_SecondaryColor;");
		putBuiltin("gl_BackSecondaryColor", "vec4", EnumShaderType.VERTEX, "gl_BackSecondaryColor=gl_SecondaryColor;");
		
		putBuiltin("gl_FragColor", "vec4", EnumShaderType.FRAGMENT, "gl_FragColor=gl_Color;");
	}
	
	private static void putBuiltin(String variable, String type) {
		new SharingVariable(variable, type, null).submit();
		builtins.add(variable);
	}
	
	private static void putBuiltin(String variable, String type, EnumShaderType shader, String normalProcess) {
		new SharingVariable(variable, type, shader, normalProcess).submit();
		builtins.add(variable);
	}
	
	public String getVariableType() {
		return type;
	}
	
	public static String[] getBuiltins() {
		return builtins.toArray(new String[0]);
	}
	
	public String getNormalProcess(EnumShaderType shaderType) {
		if(normalProcessMap == null) {
			if(shaderTarget == shaderType) return normalProcess;
			else return null;
		}
		return normalProcessMap.get(shaderType);
	}
	
	public static SharingVariable getVariable(String name) {
		return nameTypeMap.get(name);
	}
}
