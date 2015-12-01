package net.aegistudio.transparent.fbo;

import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import net.aegistudio.transparent.FeatureUnsupportedException;
import net.aegistudio.transparent.UnallocatableException;

public class FrameBufferObject {
	protected int fboId = 0;
	
	public void create() throws FeatureUnsupportedException {
		if(this.fboId == 0) {
			if(!GLContext.getCapabilities().GL_ARB_framebuffer_object)
				throw new FeatureUnsupportedException("frame buffer object");
			this.fboId = ARBFramebufferObject.glGenFramebuffers();
		}
	}
	
	public void push() {
		ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, this.fboId);
		int fbo_status = ARBFramebufferObject.glCheckFramebufferStatus(ARBFramebufferObject.GL_FRAMEBUFFER);
		if(fbo_status == ARBFramebufferObject.GL_FRAMEBUFFER_COMPLETE)
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		else throw new UnallocatableException(this);
	}
	
	public void pop() {
		GL11.glPopAttrib();
		ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, 0);
	}
	
	public void destroy() {
		if(this.fboId != 0) {
			ARBFramebufferObject.glDeleteFramebuffers(fboId);
			this.fboId = 0;
		}
	}
	
	public int getFrameBufferId() {
		return this.fboId;
	}
}
