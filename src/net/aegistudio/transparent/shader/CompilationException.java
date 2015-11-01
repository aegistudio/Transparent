package net.aegistudio.transparent.shader;

/**
 * This exception always happen at compilation phase, at which
 * the source code of shader are compiled, and become shader objects.
 * @author aegistudio
 */

@SuppressWarnings("serial")
public class CompilationException extends ShaderException {
	private final ShaderObject shaderObject;
	
	public CompilationException(ShaderObject shaderObject, String errorMessage) {
		super(errorMessage, "compiling");
		this.shaderObject = shaderObject;
	}
	
	public ShaderObject getErrorShaderObject() {
		return this.shaderObject;
	}
}
