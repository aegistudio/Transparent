package net.aegistudio.transparent.vao;

import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GLContext;

import net.aegistudio.transparent.FeatureUnsupportedException;
import net.aegistudio.transparent.UnallocatableException;
import net.aegistudio.transparent.UninitializedException;

public class VertexArrayObject
{
	private int arrayObjectId = 0;
	
	public void create() throws UnallocatableException, FeatureUnsupportedException {
		if(this.arrayObjectId != 0) return;
		if(!GLContext.getCapabilities().GL_ARB_vertex_array_object) throw new FeatureUnsupportedException("vertex array object");
		this.arrayObjectId = ARBVertexArrayObject.glGenVertexArrays();
		if(this.arrayObjectId == 0) throw new UnallocatableException(this);
	}
	
	public void bind() throws UnallocatableException, FeatureUnsupportedException {
		if(arrayObjectId == 0) throw new UninitializedException(this);
		ARBVertexArrayObject.glBindVertexArray(arrayObjectId);
	}
	
	public void unbind() {
		ARBVertexArrayObject.glBindVertexArray(0);
	}
	
	public void destroy() {
		if(this.arrayObjectId == 0) return;
		ARBVertexArrayObject.glDeleteVertexArrays(arrayObjectId);
		this.arrayObjectId = 0;
	}
}