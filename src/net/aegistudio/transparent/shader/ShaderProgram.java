package net.aegistudio.transparent.shader;

import java.util.Stack;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GLContext;

import net.aegistudio.transparent.FeatureUnsupportedException;
import net.aegistudio.transparent.UnallocatableException;
import net.aegistudio.transparent.UninitializedException;

/**
 * A shader program should replace all current states of original pipe.
 * Any two shader programs are exclusive and so we need a higher level
 * solution for it to make it compatible.
 * @author aegistudio
 */

public class ShaderProgram {
	private final ShaderObject[] shaderObjects;
	private int shaderProgramId;
	protected long lastBuildTimestamp;
	
	public ShaderProgram(ShaderObject... shaderObjects) {
		this.shaderObjects = shaderObjects;
		this.shaderProgramId = 0;
		this.lastBuildTimestamp = 0;
	}
	
	public void create() throws FeatureUnsupportedException, 
			UnallocatableException, ShaderException {
		if(shaderProgramId == 0) try {
			this.allocate();
			this.assemble(this.shaderObjects);
		}
		catch(Exception e) {
			this.destroy();
			throw e;
		}
	}
	
	/**
	 * Allocate space for a shader program.
	 * 
	 * This method is extracted out in order to make shader program in TransparentX come
	 * into use. Reducing coupling.
	 * 
	 * @throws FeatureUnsupportedException
	 * @throws UnallocatableException
	 */
	
	protected void allocate() throws FeatureUnsupportedException, UnallocatableException {
		if(!GLContext.getCapabilities().GL_ARB_shader_objects)
			throw new FeatureUnsupportedException("shader");
		
		shaderProgramId = ARBShaderObjects.glCreateProgramObjectARB();
		if(shaderProgramId == 0) throw new UnallocatableException(this);
	}
	
	/**
	 * Assemble a shader program with given shader objects.
	 * 
	 * This method is extracted out in order to make shader program in TransparentX come
	 * into use. Reducing coupling.
	 * 
	 * @param shaderObjects
	 * @throws ShaderException
	 */
	
	protected void assemble(ShaderObject[] shaderObjects) throws ShaderException {
		for(ShaderObject shaderObject : shaderObjects)
			attach(shaderObject);
		
		ARBShaderObjects.glLinkProgramARB(shaderProgramId);
		String linkageErrorInfo = ShaderException.getShaderErrorMessage(
				ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB, shaderProgramId);
		if(linkageErrorInfo != null)
			throw new LinkageException(this, linkageErrorInfo);
		
		ARBShaderObjects.glValidateProgramARB(shaderProgramId);
		String validationErrorInfo = ShaderException.getShaderErrorMessage(
				ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB, shaderProgramId);
		if(validationErrorInfo != null)
			throw new ValidationException(this, validationErrorInfo);
		
		lastBuildTimestamp = System.nanoTime();
	}
	
	private static final Stack<ShaderProgram> shaderProgramStack = new Stack<ShaderProgram>();
	private static ShaderProgram currentShaderProgram = null;
	
	int getShaderProgramId() {
		return this.shaderProgramId;
	}
	
	static ShaderProgram getCurrentProgram() {
		return currentShaderProgram;
	}
	
	public void pushShaderProgram() {
		if(shaderProgramId == 0) throw new UninitializedException(this);
		shaderProgramStack.push(currentShaderProgram);
		currentShaderProgram = this;
		
		ARBShaderObjects.glUseProgramObjectARB(shaderProgramId);
	}
	
	public void popShaderProgram() {
		currentShaderProgram = shaderProgramStack.pop();
		if(currentShaderProgram != null)
			ARBShaderObjects.glUseProgramObjectARB(currentShaderProgram.shaderProgramId);
		else ARBShaderObjects.glUseProgramObjectARB(0);
	}
	
	public void destroy() {
		if(this.shaderProgramId != 0) {
			for(ShaderObject shader : shaderObjects) detach(shader);
			ARBShaderObjects.glDeleteObjectARB(shaderProgramId);
			this.shaderProgramId = 0;
			this.lastBuildTimestamp = 0;
		}
	}
	
	public long getLastBuildTimestamp() {
		return lastBuildTimestamp;
	}
	
	public Uniform getUniform(EnumShaderData dataType, String name) {
		return new Uniform(this, dataType, name);
	}
	
	public VertexAttribute getVertexAttribute(EnumShaderData dataType, String name) {
		return new VertexAttribute(this, dataType, name);
	}
	
	protected void attach(ShaderObject shaderObject) 
			throws FeatureUnsupportedException, CompilationException {
		shaderObject.attach(shaderProgramId);
	}
	
	protected void detach(ShaderObject shaderObject) {
		shaderObject.detach(shaderProgramId);
	}
}
