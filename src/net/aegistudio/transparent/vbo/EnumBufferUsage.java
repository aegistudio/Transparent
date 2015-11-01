package net.aegistudio.transparent.vbo;

import org.lwjgl.opengl.ARBBufferObject;

import net.aegistudio.transparent.Constant;

/****************************************************
 * This enumeration depicts the range for the value for
 * the buffer usage parameter.
 * 
 * @author aegistudio
 ***************************************************/

public enum EnumBufferUsage implements Constant{
	STATIC_DRAW(ARBBufferObject.GL_STATIC_DRAW_ARB),
	STATIC_READ(ARBBufferObject.GL_STATIC_READ_ARB),
	STATIC_COPY(ARBBufferObject.GL_STATIC_COPY_ARB),
	
	STREAM_DRAW(ARBBufferObject.GL_STREAM_DRAW_ARB),
	STREAM_READ(ARBBufferObject.GL_STREAM_READ_ARB),
	STREAM_COPY(ARBBufferObject.GL_STREAM_COPY_ARB),
	
	DYNAMIC_DRAW(ARBBufferObject.GL_DYNAMIC_DRAW_ARB),
	DYNAMIC_READ(ARBBufferObject.GL_DYNAMIC_READ_ARB),
	DYNAMIC_COPY(ARBBufferObject.GL_DYNAMIC_COPY_ARB);
	
	private final int bufferUsageId;
	
	private EnumBufferUsage(int bufferUsageId) {
		this.bufferUsageId = bufferUsageId;
	}

	@Override
	public int getValue() {
		return bufferUsageId;
	}
}