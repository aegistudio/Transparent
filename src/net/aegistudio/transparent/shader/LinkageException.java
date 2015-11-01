package net.aegistudio.transparent.shader;

@SuppressWarnings("serial")
public class LinkageException extends ShaderException {
	private final ShaderProgram shaderProgram;
	
	public LinkageException(ShaderProgram shaderProgram, String errorMessage) {
		super(errorMessage, "linking");
		this.shaderProgram = shaderProgram;
	}
	
	public ShaderProgram getErrorShaderProgram() {
		return this.shaderProgram;
	}
}
