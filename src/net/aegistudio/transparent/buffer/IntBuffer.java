package net.aegistudio.transparent.buffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;

import net.aegistudio.transparent.EnumDataType;

public class IntBuffer implements Buffer{

	private final java.nio.IntBuffer intBuffer;
	private final int elements;
	
	public IntBuffer(int[] intValue) {
		this.intBuffer = BufferUtils.createIntBuffer(intValue.length);
		this.intBuffer.put(intValue);
		this.intBuffer.flip();
		this.elements = intValue.length;
	}
	
	public IntBuffer(java.nio.IntBuffer intValue) {
		this.intBuffer = intValue;
		this.elements = intBuffer.capacity();
	}
	
	@Override
	public void bufferData(int target, int usage) {
		ARBVertexBufferObject.glBufferDataARB(target, intBuffer, usage);
	}
	
	@Override
	public EnumDataType getType() {
		return EnumDataType.INT;
	}

	@Override
	public int getElements() {
		return elements;
	}
	
}
