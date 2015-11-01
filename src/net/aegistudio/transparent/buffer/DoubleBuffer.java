package net.aegistudio.transparent.buffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;

import net.aegistudio.transparent.EnumDataType;

public class DoubleBuffer implements Buffer {
	private final java.nio.DoubleBuffer doubleBuffer;
	private final int elements;
	
	public DoubleBuffer(double[] doubleValue) {
		this.doubleBuffer = BufferUtils.createDoubleBuffer(doubleValue.length);
		this.doubleBuffer.put(doubleValue);
		this.doubleBuffer.flip();
		this.elements = doubleValue.length;
	}
	
	public DoubleBuffer(java.nio.DoubleBuffer doubleBuffer) {
		this.doubleBuffer = doubleBuffer;
		this.elements = doubleBuffer.capacity();
	}

	@Override
	public void bufferData(int target, int usage) {
		ARBVertexBufferObject.glBufferDataARB(target, doubleBuffer, usage);
	}
	
	@Override
	public EnumDataType getType() {
		return EnumDataType.DOUBLE;
	}

	@Override
	public int getElements() {
		return elements;
	}
}
