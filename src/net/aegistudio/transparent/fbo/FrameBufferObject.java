package net.aegistudio.transparent.fbo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import net.aegistudio.transparent.FeatureUnsupportedException;
import net.aegistudio.transparent.UnallocatableException;

public class FrameBufferObject {
	protected int fboId = 0;
	
	protected final int x, y, w, h;
	public FrameBufferObject(int x, int y, int w, int h) {
		this.x = x; this.y = y; this.w = w; this.h = h;
	}
	
	public void create() throws FeatureUnsupportedException {
		if(this.fboId == 0) {
			if(!GLContext.getCapabilities().GL_ARB_framebuffer_object)
				throw new FeatureUnsupportedException("frame buffer object");
			this.fboId = ARBFramebufferObject.glGenFramebuffers();
			
			this.updateAttachment();
		}
	}
	
	public void attach(FrameBufferAttachment attachment, Renderable renderable) {
		this.attachment.put(attachment, renderable);
	}
	
	private final HashMap<FrameBufferAttachment, Renderable> attachment 
		= new HashMap<FrameBufferAttachment, Renderable>();
	private final HashMap<FrameBufferAttachment, Renderable> activeAttachment
		= new HashMap<FrameBufferAttachment, Renderable>();
	
	private void updateAttachment() {
		if(attachment.isEmpty()) return;
		
		ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, fboId);
		Iterator<Entry<FrameBufferAttachment, Renderable>> iterator = attachment.entrySet().iterator();
		while(iterator.hasNext()) {
			Entry<FrameBufferAttachment, Renderable> attachmentEntry = iterator.next();
			Renderable original = activeAttachment.get(attachmentEntry.getKey());
			if(original != null) 
				if(!original.detach(attachmentEntry.getKey())) continue;
			
			if(attachmentEntry.getValue() != null) {
				if(attachmentEntry.getValue().attach(attachmentEntry.getKey())) {
					activeAttachment.put(attachmentEntry.getKey(), attachmentEntry.getValue());	
					iterator.remove();
				}
			}
		}
		ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, 0);
	}
	
	public void push() {
		this.updateAttachment();
		ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, this.fboId);
		int fbo_status = ARBFramebufferObject.glCheckFramebufferStatus(ARBFramebufferObject.GL_FRAMEBUFFER);
		if(fbo_status == ARBFramebufferObject.GL_FRAMEBUFFER_COMPLETE) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glViewport(x, y, w, h);
		}
		else throw new UnallocatableException(this);
		
	}
	
	public void pop() {
		GL11.glPopAttrib();
		this.updateAttachment();
		ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, 0);
	}
	
	public void destroy() {
		if(this.fboId != 0) {
			ARBFramebufferObject.glDeleteFramebuffers(fboId);
			this.fboId = 0;
			
			this.activeAttachment.clear();
			this.attachment.clear();
		}
	}
	
	public int getFrameBufferId() {
		return this.fboId;
	}
}
