package net.aegistudio.transparent.vao;

import net.aegistudio.transparent.vbo.VertexBufferObject;

public class ArrayPointerEntry {
	
	public final ArrayPointer arrayPointer;
	public final VertexBufferObject vbo;
	public final int size, offset, stride;
	
	public ArrayPointerEntry(ArrayPointer arrayPointer, VertexBufferObject vbo, int size, int offset, int stride) {
		this.arrayPointer = arrayPointer;
		this.vbo = vbo;
		this.size = size;
		this.offset = offset;
		this.stride = stride;
	}
	
	public ArrayPointerEntry(ArrayPointer arrayPointer, VertexBufferObject vbo, int size) {
		this(arrayPointer, vbo, size, 0, 0);
	}
	
	public ArrayPointerEntry(ArrayPointer arrayPointer, VertexBufferObject vbo) {
		this(arrayPointer, vbo, arrayPointer.getDefaultSize());
	}
	
	public void arrayPointer() {
		this.arrayPointer.arrayPointer(size, vbo.getType().dataTypeId, stride, offset);
	}
}
