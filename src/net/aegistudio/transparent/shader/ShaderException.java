package net.aegistudio.transparent.shader;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("serial")
public class ShaderException extends Exception {
	protected final String errorMessage;
	
	public ShaderException(String errorMessage, String errorPhase) {
		super(String.format("Error while %s, caused by: %s", errorPhase, errorMessage));
		this.errorMessage = errorMessage;
	}
	
	public String getErrorMessage() {
		return this.errorMessage;
	}
	
	static String getShaderErrorMessage(int parameterId, int shaderId) {
		
		if(ARBShaderObjects.glGetObjectParameteriARB(shaderId,
				parameterId) == GL11.GL_FALSE)
			return ARBShaderObjects.glGetInfoLogARB(shaderId, 
					ARBShaderObjects.glGetObjectParameteriARB(shaderId,
							ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
		else return null;
	}
}
