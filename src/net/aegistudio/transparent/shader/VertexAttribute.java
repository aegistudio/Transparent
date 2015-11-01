package net.aegistudio.transparent.shader;

import org.lwjgl.opengl.ARBVertexShader;

import net.aegistudio.transparent.UnallocatableException;
import net.aegistudio.transparent.UninitializedException;

public class VertexAttribute {
	
	private ShaderProgram shaderProgram;
	private EnumShaderData attributeType;
	private String attributeName;
	
	public VertexAttribute(ShaderProgram shaderProgram, EnumShaderData attributeType, String attributeName) {
		this.shaderProgram = shaderProgram;
		this.attributeType = attributeType;
		this.attributeName = attributeName;
	}
	
	public VertexAttribute(EnumShaderData attributeType, String attributeName) {
		this(null, attributeType, attributeName);
	}
	
	private long timestamp = 0;
	private int vertexAttributeId = -1;
	
	private ShaderProgram currentProgram = null; //Used Only When targetProgram == null.
	
	public int getVertexAttributeId() {
		if(shaderProgram != null) {
			if(shaderProgram.getShaderProgramId() == 0)
				 throw new UninitializedException(this.shaderProgram);
			if(vertexAttributeId == -1 
					|| shaderProgram.getLastBuildTimestamp() > timestamp) {
				vertexAttributeId = ARBVertexShader.glGetAttribLocationARB(
						shaderProgram.getShaderProgramId(), attributeName);
				timestamp = shaderProgram.getLastBuildTimestamp();
				if(vertexAttributeId == -1) throw new UnallocatableException(this);
			}
		}
		else {
			if(ShaderProgram.getCurrentProgram() == null) return -1;
			if(currentProgram != ShaderProgram.getCurrentProgram()
					|| currentProgram.getLastBuildTimestamp() > timestamp) {

				currentProgram = ShaderProgram.getCurrentProgram();
				vertexAttributeId = ARBVertexShader.glGetAttribLocationARB(
						currentProgram.getShaderProgramId(), attributeName);
				timestamp = currentProgram.getLastBuildTimestamp();
			}
		}
		return vertexAttributeId;
	}
	
	public EnumShaderData getAttributeType() {
		return this.attributeType;
	}
	
	public void set(Object... value) {
		Object[] arguments = new Object[value.length + 1];
		for(int i = 0; i < value.length; i ++)
			arguments[i + 1] = value[i];
		arguments[0] = this.getVertexAttributeId();
		attributeType.vertexAttribute(arguments);
	}
	
	public void bindVertexArrayPointer(int index) {
		if(this.getVertexAttributeId() == -1) return;
		if(shaderProgram != null)
			ARBVertexShader.glBindAttribLocationARB(shaderProgram.getShaderProgramId(),
					index, this.attributeName);
		else if(ShaderProgram.getCurrentProgram() != null) {
			ARBVertexShader.glBindAttribLocationARB(
					ShaderProgram.getCurrentProgram().getShaderProgramId(),
					index, this.attributeName);		
		}
	}
}
