package net.aegistudio.transparent.shader;

@SuppressWarnings("serial")
public class ValidationException extends ShaderException {
	private final ShaderProgram shaderProgram;
	
	public ValidationException(ShaderProgram shaderProgram, String errorMessage) {
		super(errorMessage, "validating");
		this.shaderProgram = shaderProgram;
	}
	
	public ShaderProgram getErrorShaderProgram() {
		return this.shaderProgram;
	}
}
