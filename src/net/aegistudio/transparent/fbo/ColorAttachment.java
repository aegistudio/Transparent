package net.aegistudio.transparent.fbo;

import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.GL11;

import net.aegistudio.transparent.FeatureUnsupportedException;

public class ColorAttachment implements FrameBufferAttachment {
	private final int index;
	public ColorAttachment(int index) {
		this.index = index;
	}
	@Override
	public int getFrameBufferAttachment() {
		int maxOffset = GL11.glGetInteger(ARBFramebufferObject.GL_MAX_COLOR_ATTACHMENTS);
		if(this.index >= maxOffset) throw new FeatureUnsupportedException(
				String.format("color attachment #%d", index));
		return ARBFramebufferObject.GL_COLOR_ATTACHMENT0 + index;
	}
	
	
}
