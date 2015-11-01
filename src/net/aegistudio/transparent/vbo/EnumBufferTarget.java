package net.aegistudio.transparent.vbo;

import org.lwjgl.opengl.ARBCopyBuffer;
import org.lwjgl.opengl.ARBPixelBufferObject;
import org.lwjgl.opengl.ARBUniformBufferObject;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL30;

import net.aegistudio.transparent.Constant;

/****************************************************
 * This enumeration depicts the range for the value for
 * the buffer target parameter.
 * 
 * @author aegistudio
 ***************************************************/

public enum EnumBufferTarget implements Constant{
	
	ARRAY(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB),
	ELEMENT_ARRAY(ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB),
	PIXEL_UNPACK(ARBPixelBufferObject.GL_PIXEL_UNPACK_BUFFER_ARB),
	PIXEL_PACK(ARBPixelBufferObject.GL_PIXEL_PACK_BUFFER_ARB),
	COPY_READ(ARBCopyBuffer.GL_COPY_READ_BUFFER),
	COPY_WRITE(ARBCopyBuffer.GL_COPY_WRITE_BUFFER),
	TRANSFORM_FEEDBACK(GL30.GL_TRANSFORM_FEEDBACK_BUFFER),
	UNIFORM(ARBUniformBufferObject.GL_UNIFORM_BUFFER);
	
	private final int bufferTargetId;
	
	private EnumBufferTarget(int value) {
		this.bufferTargetId = value;
	}

	@Override
	public int getValue() {
		return bufferTargetId;
	}
}
