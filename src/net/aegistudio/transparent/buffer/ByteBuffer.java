package net.aegistudio.transparent.buffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;

import net.aegistudio.transparent.EnumDataType;

public class ByteBuffer implements Buffer{
	
	private final java.nio.ByteBuffer byteBuffer;
	private final int elements;
	
	public ByteBuffer(byte[] byteValue) {
		this.byteBuffer = BufferUtils.createByteBuffer(byteValue.length);
		this.byteBuffer.put(byteValue);
		this.byteBuffer.flip();
		this.elements = byteValue.length;
	}
	
	public ByteBuffer(java.nio.ByteBuffer byteBuffer) {
		this.byteBuffer = byteBuffer;
		this.elements = byteBuffer.capacity();
	}

	@Override
	public void bufferData(int target, int usage) {
		ARBVertexBufferObject.glBufferDataARB(target, byteBuffer, usage);
	}

	@Override
	public EnumDataType getType() {
		return EnumDataType.BYTE;
	}

	@Override
	public int getElements() {
		return elements;
	}
}
