package net.aegistudio.transparent.fbo;

import org.lwjgl.opengl.ARBFramebufferObject;

public enum EnumBufferAttachment implements FrameBufferAttachment {
	COLOR(ARBFramebufferObject.GL_COLOR_ATTACHMENT0),
	DEPTH(ARBFramebufferObject.GL_DEPTH_ATTACHMENT),
	DEPTH_STENCIL(ARBFramebufferObject.GL_DEPTH_STENCIL_ATTACHMENT),
	STENCIL(ARBFramebufferObject.GL_STENCIL_ATTACHMENT);
	
	public final int bufferAttachment;
	
	private EnumBufferAttachment(int bufferAttachment) {
		this.bufferAttachment = bufferAttachment;
	}
	
	@Override
	public int getFrameBufferAttachment() {
		return bufferAttachment;
	}

}
