package net.aegistudio.transparent.vao;

import org.lwjgl.opengl.GL11;

import net.aegistudio.transparent.EnumPrimitive;
import net.aegistudio.transparent.FeatureUnsupportedException;
import net.aegistudio.transparent.UnallocatableException;
import net.aegistudio.transparent.vbo.VertexBufferObject;

public class Model {
	
	private final ArrayPointerEntry[] entries;
	private int vertices = 0;
	private final VertexArrayObject vao = new VertexArrayObject();
	private boolean hasCreated = false;
	private boolean scoping;
	
	// When IBO is designated, use drawElements instead of drawArrays.
	private VertexBufferObject ibo = null;
	private int iboType = 0;
	
	public Model(ArrayPointerEntry... bufferRelation) {
		this(false, bufferRelation);
	}
	
	public Model(boolean scoping, ArrayPointerEntry... bufferRelation) {
		this.scoping = scoping;
		boolean hasVertex = false;
		this.entries = bufferRelation;
		
		for(ArrayPointerEntry ape : bufferRelation)
			if(ape.arrayPointer == EnumArrayPointer.VERTEX) {
				if(hasVertex) throw new IllegalArgumentException("Index array is aleady designated!");
				hasVertex = true;
				if(this.ibo == null)
					this.vertices = ape.vbo.getMaximumElements() / (ape.offset + ape.stride + ape.size);
			}
			else if(ape instanceof IndexPointerEntry) {
				if(this.ibo != null) throw new IllegalArgumentException("Index buffer object is already designated!");
				this.ibo = ape.vbo;
				this.vertices = ape.vbo.getMaximumElements();
				switch(ape.vbo.getType()) {
					case INT:
						this.iboType = GL11.GL_UNSIGNED_INT;
					break;
					case SHORT:
						this.iboType = GL11.GL_UNSIGNED_SHORT;
					break;
					case BYTE:
						this.iboType = GL11.GL_UNSIGNED_BYTE;
					break;
					default:
						throw new IllegalArgumentException("Invalid data type for index buffer object!");
				}
			}
		
		if(!hasVertex) 
			throw new IllegalArgumentException("The vertices to be rendered has not been set");
	}
	
	public void create() throws FeatureUnsupportedException, UnallocatableException {
		if(!hasCreated) {
			this.vao.create();
			this.vao.bind();
			for(ArrayPointerEntry entry : entries) {
				entry.arrayPointer.enable();
				if(scoping) entry.vbo.create();
				entry.vbo.bind();
				entry.arrayPointer();
				entry.vbo.unbind();
			}
			this.vao.unbind();

			for(ArrayPointerEntry entry : entries) 
				entry.arrayPointer.disable();
			hasCreated = true;
		}
	}
	
	public void draw(EnumPrimitive primitive) throws FeatureUnsupportedException, UnallocatableException {
		this.vao.bind();
		for(ArrayPointerEntry entry : entries) 
			entry.arrayPointer.draw();
		
		if(this.ibo == null) GL11.glDrawArrays(primitive.getValue(), 0, vertices);
		else GL11.glDrawElements(primitive.getValue(), vertices, this.iboType, 0);
		
		this.vao.unbind();
	}
	
	public void destroy() {
		this.vao.destroy();
		if(scoping) for(ArrayPointerEntry entry : entries) entry.vbo.destroy();
	}
}
