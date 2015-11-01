package net.aegistudio.transparent.buffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;

import net.aegistudio.transparent.EnumDataType;

public class FloatBuffer implements Buffer{
	private final java.nio.FloatBuffer floatBuffer;
	private final int elements;
	
	public FloatBuffer(float[] floatValue) {
		this.floatBuffer = BufferUtils.createFloatBuffer(floatValue.length);
		this.floatBuffer.put(floatValue);
		this.floatBuffer.flip();
		this.elements = floatValue.length;
	}
	
	public FloatBuffer(java.nio.FloatBuffer floatBuffer) {
		this.floatBuffer = floatBuffer;
		this.elements = floatBuffer.capacity();
	}

	@Override
	public void bufferData(int target, int usage) {
		ARBVertexBufferObject.glBufferDataARB(target, floatBuffer, usage);
	}
	
	@Override
	public EnumDataType getType() {
		return EnumDataType.FLOAT;
	}

	@Override
	public int getElements() {
		return elements;
	}
}
