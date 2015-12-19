package net.aegistudio.transparent.fbo;

import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.GLContext;

import net.aegistudio.transparent.FeatureUnsupportedException;
import net.aegistudio.transparent.image.EnumPixelFormat;

public class RenderBuffer implements Renderable {

	private final EnumPixelFormat internalFormat;
	private final int width, height;
	
	private int rboId;
	public RenderBuffer(EnumPixelFormat internalFormat, int width, int height) {
		this.internalFormat = internalFormat;
		this.width = width; this.height = height;
	}
	
	public void create() {
		if(rboId == 0) {
			if(!GLContext.getCapabilities().GL_ARB_framebuffer_object)
				throw new FeatureUnsupportedException("frame buffer object");
			rboId = ARBFramebufferObject.glGenRenderbuffers();
			ARBFramebufferObject.glBindRenderbuffer(ARBFramebufferObject.GL_RENDERBUFFER, rboId);
			ARBFramebufferObject.glRenderbufferStorage(ARBFramebufferObject.GL_RENDERBUFFER, 
					internalFormat.glPixelFormatId, width, height);
			ARBFramebufferObject.glBindRenderbuffer(ARBFramebufferObject.GL_RENDERBUFFER, 0);
		}
	}
	
	@Override
	public boolean attach(FrameBufferAttachment attachment) {
		if(rboId == 0) return false;
		ARBFramebufferObject.glFramebufferRenderbuffer(ARBFramebufferObject.GL_FRAMEBUFFER, 
				attachment.getFrameBufferAttachment(), ARBFramebufferObject.GL_RENDERBUFFER, rboId);
		return true;
	}

	@Override
	public boolean detach(FrameBufferAttachment attachment) {
		ARBFramebufferObject.glFramebufferRenderbuffer(ARBFramebufferObject.GL_FRAMEBUFFER, 
				attachment.getFrameBufferAttachment(), ARBFramebufferObject.GL_RENDERBUFFER, 0);
		return true;
	}

	public void destroy() {
		if(rboId != 0) 
			 ARBFramebufferObject.glDeleteRenderbuffers(rboId);
	}
}
