package net.aegistudio.transparent.vao;

import org.lwjgl.opengl.GL11;

import net.aegistudio.transparent.EnumPrimitive;
import net.aegistudio.transparent.FeatureUnsupportedException;
import net.aegistudio.transparent.UnallocatableException;

public class Model {
	
	private final ArrayPointerEntry[] entries;
	private int vertices = 0;
	private final VertexArrayObject vao = new VertexArrayObject();
	private boolean hasCreated = false;
	private boolean scoping;
	
	public Model(ArrayPointerEntry... bufferRelation) {
		this(false, bufferRelation);
	}
	
	public Model(boolean scoping, ArrayPointerEntry... bufferRelation) {
		this.scoping = scoping;
		boolean hasVertex = false;
		this.entries = bufferRelation;
		
		for(ArrayPointerEntry ape : bufferRelation)
			if(ape.arrayPointer == EnumArrayPointer.VERTEX) {
				hasVertex = true;
				this.vertices = ape.vbo.getMaximumElements() / (ape.offset + ape.stride + ape.size);
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
		GL11.glDrawArrays(primitive.getValue(), 0, vertices);
		this.vao.unbind();
	}
	
	public void destroy() {
		this.vao.destroy();
		if(scoping) for(ArrayPointerEntry entry : entries) entry.vbo.destroy();
	}
}
