package net.aegistudio.transparent.shader;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GLContext;

import net.aegistudio.transparent.FeatureUnsupportedException;
import net.aegistudio.transparent.UnallocatableException;

/**
 * Shader objects are just like a pre-processed and compiled c-code
 * object. It's waiting for linking together to form a shader program.
 * @author aegistudio
 */

public class ShaderObject {
	private final String sourceCode;
	private final EnumShaderType shaderType;
	private int shaderObjectId;
	
	public ShaderObject(EnumShaderType shaderType, String sourceCode) {
		this.sourceCode = sourceCode;
		this.shaderType = shaderType;
		this.shaderObjectId = 0;
	}
	
	public void create() throws FeatureUnsupportedException, CompilationException{
		if(this.shaderObjectId == 0) {
			if(!GLContext.getCapabilities().GL_ARB_shader_objects)
				throw new FeatureUnsupportedException("shader");
			
			if(!shaderType.checkCapability())
				throw new FeatureUnsupportedException(shaderType.shaderTypeName);
			
			shaderObjectId = ARBShaderObjects.glCreateShaderObjectARB(shaderType.getValue());
			if(shaderObjectId == 0) throw new UnallocatableException(this);
			
			ARBShaderObjects.glShaderSourceARB(shaderObjectId, sourceCode);
			ARBShaderObjects.glCompileShaderARB(shaderObjectId);
			String failureInfo = ShaderException.getShaderErrorMessage(ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB, shaderObjectId);
			if(failureInfo != null) {
				CompilationException compileFailureException = new CompilationException(this, failureInfo);
				this.destroy();
				throw compileFailureException;
			}
		}
	}
	
	public void destroy() {
		if(this.shaderObjectId != 0) {
			ARBShaderObjects.glDeleteObjectARB(this.shaderObjectId);
			this.shaderObjectId = 0;
		}
	}
	
	void attach(int shaderProgramId) throws FeatureUnsupportedException, CompilationException {
		if(this.shaderObjectId == 0) this.create();
		ARBShaderObjects.glAttachObjectARB(shaderProgramId, shaderObjectId);
	}
	
	void detach(int shaderProgramId) {
		if(this.shaderObjectId != 0) {
			ARBShaderObjects.glDetachObjectARB(shaderProgramId, shaderObjectId);
		}
	}
}
