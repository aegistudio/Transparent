package net.aegistudio.transparent.shader;

import java.util.Stack;
import java.util.TreeMap;

import org.lwjgl.opengl.ARBShaderObjects;

import net.aegistudio.transparent.UnallocatableException;
import net.aegistudio.transparent.model.Effect;

/**
 * Will set a uniform value in a shader program.
 * 
 * When targetProgram is not assigned, the current 
 * active shader program will be the target to set value.
 * 
 * @author aegistudio
 */

public class Uniform implements Effect {
	private final ShaderProgram targetProgram;

	private final String uniformName;
	private final EnumShaderData shaderData;
	
	private long timestamp;
	private int uniformId;
	private Object[] value;
	private Stack<Object[]> targetStack;
	
	public Uniform(ShaderProgram targetProgram, EnumShaderData uniformData, String uniformName) {
		this.targetProgram = targetProgram;
		this.uniformName = uniformName;
		this.shaderData = uniformData;
		this.timestamp = 0;
	}
	
	public Uniform(EnumShaderData uniformData, String uniformName) {
		this(null, uniformData, uniformName);
	}
	
	public void set(Object... value) {
		this.value = new Object[value.length + 1];
		for(int i = 0; i < value.length; i ++)
			this.value[i + 1] = value[i];
	}

	static TreeMap<String, Stack<Object[]>> values = new TreeMap<String, Stack<Object[]>>();
	
	ShaderProgram currentProgram;	//Only Used When targetProgram == null.
	
	@Override
	public void use() {
		/**
		 * Since the special order of effect activation, the uniform will be got at using phase
		 * rather than initializing phase, please notice.
		 */
		if(targetProgram != null) {
			if(targetProgram.getShaderProgramId() == 0) return;
			if(targetStack == null 
					|| targetProgram.getLastBuildTimestamp() > timestamp) {
				uniformId = ARBShaderObjects.glGetUniformLocationARB(
						targetProgram.getShaderProgramId(), uniformName);
				this.getTargetStack();
				timestamp = targetProgram.getLastBuildTimestamp();
			}
			if(uniformId == -1) throw new UnallocatableException(this);	
		}
		else {
			if(ShaderProgram.getCurrentProgram() == null) return;
			if(targetStack == null)
				this.getTargetStack();
			if(currentProgram != ShaderProgram.getCurrentProgram()
					|| currentProgram.getLastBuildTimestamp() > timestamp) {
				uniformId = ARBShaderObjects.glGetUniformLocationARB(
						ShaderProgram.getCurrentProgram().getShaderProgramId(), uniformName);
				currentProgram = ShaderProgram.getCurrentProgram();
				timestamp = currentProgram.getLastBuildTimestamp();
			}
			if(uniformId == -1) return;
		}

		if(value != null) {
			this.value[0] = uniformId;
			this.shaderData.uniform(value);
			if(targetStack != null) this.targetStack.push(value);
		}
	}

	protected void getTargetStack() {
		if(!values.containsKey(uniformName))
			values.put(uniformName, new Stack<Object[]>());
		targetStack = values.get(uniformName);
	}
	
	@Override
	public void recover() {
		if(this.targetStack != null) {
			if(!this.targetStack.isEmpty())
				this.shaderData.uniform(this.targetStack.pop());
		}
	}

	@Override
	public void destroy() {
		// Do nothing.
	}

	@Override
	public void create() {
		// Do nothing.
	}
}
