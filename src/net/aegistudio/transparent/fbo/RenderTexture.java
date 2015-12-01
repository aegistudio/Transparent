package net.aegistudio.transparent.fbo;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.GL11;

import net.aegistudio.transparent.UnallocatableException;
import net.aegistudio.transparent.image.EnumPixelFormat;
import net.aegistudio.transparent.texture.EnumTexture;
import net.aegistudio.transparent.texture.Texture;

public class RenderTexture extends Texture {
	private final FrameBufferObject fbo;
	private final int textureWidth, textureHeight;
	private final EnumPixelFormat pixelFormat;
	private final FrameBufferAttachment[] attachments;
	
	public RenderTexture(int textureWidth, int textureHeight, 
			EnumPixelFormat internalFormat, FrameBufferAttachment... attachments) {
		this(EnumTexture.SURFACE, textureWidth, textureHeight, internalFormat, attachments);
	}
	
	public RenderTexture(EnumTexture texTarget, int textureWidth, int textureHeight, 
			EnumPixelFormat internalFormat, FrameBufferAttachment... attachments) {
		super(texTarget);
		this.fbo = new FrameBufferObject();
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.pixelFormat = internalFormat;
		this.attachments = attachments;
	}

	@Override
	public void create(int mipmapLevel) {
		if(this.textureId == 0) {
			this.textureId = GL11.glGenTextures();
			if(this.textureId == 0) throw new UnallocatableException(this);
			
			int previousTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
			
			GL11.glBindTexture(textureType.getValue(), this.textureId);
			super.setupTextureParameter();
			GL11.glTexImage2D(textureType.getValue(), mipmapLevel, this.pixelFormat.getPixelFormatTypeValue(), 
					this.textureWidth, this.textureHeight, 0, this.pixelFormat.getPixelFormatTypeValue(),
					this.pixelFormat.getPixelFormatDataValue(), (ByteBuffer)null);
			GL11.glBindTexture(textureType.getValue(), previousTexture);
			
			this.fbo.create();
			int fboId = this.fbo.getFrameBufferId();
			ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, fboId);
			for(FrameBufferAttachment attachment : this.attachments) 
				ARBFramebufferObject.glFramebufferTexture2D(ARBFramebufferObject.GL_FRAMEBUFFER, 
						attachment.getFrameBufferAttachment(), textureType.getValue(), textureId, mipmapLevel);
			ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, 0);
		}
	}

	public void destroy() {
		super.destroy();
		this.fbo.destroy();
	}
	
	public void push() {
		this.fbo.push();
		GL11.glViewport(0, 0, textureWidth, textureHeight);
	}
	
	public void pop() {
		this.fbo.pop();
	}
	
	@Override
	protected void subbindTexture() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
	}
}
