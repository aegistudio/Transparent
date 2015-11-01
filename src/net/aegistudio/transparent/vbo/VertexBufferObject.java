package net.aegistudio.transparent.vbo;

import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GLContext;

import net.aegistudio.transparent.EnumDataType;
import net.aegistudio.transparent.FeatureUnsupportedException;
import net.aegistudio.transparent.UnallocatableException;
import net.aegistudio.transparent.UninitializedException;
import net.aegistudio.transparent.buffer.Buffer;

public class VertexBufferObject {
	private final int bufferTarget;
	private final int bufferUsage;
	
	private final Buffer buffer;
	
	private int bufferId = 0;
	
	public <N extends Number> VertexBufferObject(EnumBufferTarget bufferTarget, EnumBufferUsage bufferUsage, Buffer buffer) {
		this.bufferTarget = bufferTarget.getValue();
		this.bufferUsage = bufferUsage.getValue();
		this.buffer = buffer;
	}
	
	public void create() throws FeatureUnsupportedException, UnallocatableException {
		if(bufferId != 0) return;
		if(!GLContext.getCapabilities().GL_ARB_vertex_buffer_object)
			throw new FeatureUnsupportedException("vertex buffer object");
		bufferId = ARBVertexBufferObject.glGenBuffersARB();
		if(bufferId == 0) throw new UnallocatableException(this);
		
		ARBVertexBufferObject.glBindBufferARB(bufferTarget, bufferId);
		buffer.bufferData(bufferTarget, bufferUsage);
		ARBVertexBufferObject.glBindBufferARB(bufferTarget, 0);
	}
	
	public void bind() {
		if(bufferId == 0) throw new UninitializedException(this);
		ARBVertexBufferObject.glBindBufferARB(bufferTarget, bufferId);
	}
	
	public void unbind() {
		ARBVertexBufferObject.glBindBufferARB(bufferTarget, 0);
	}
	
	public void destroy() {
		if(bufferId == 0) return;
		ARBVertexBufferObject.glDeleteBuffersARB(bufferId);
	}
	
	public EnumDataType getType() {
		return buffer.getType();
	}
	
	public int getMaximumElements() {
		return buffer.getElements();
	}
}
