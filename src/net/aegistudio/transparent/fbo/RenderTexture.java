package net.aegistudio.transparent.fbo;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.GL11;

import net.aegistudio.transparent.UnallocatableException;
import net.aegistudio.transparent.image.EnumPixelFormat;
import net.aegistudio.transparent.texture.EnumTexture;
import net.aegistudio.transparent.texture.Texture;

public class RenderTexture extends Texture implements Renderable {
	private final int textureWidth, textureHeight;
	private final EnumPixelFormat pixelFormat;
	
	public RenderTexture(int textureWidth, int textureHeight, EnumPixelFormat internalFormat) {
		this(EnumTexture.SURFACE, textureWidth, textureHeight, internalFormat);
	}
	
	public RenderTexture(EnumTexture texTarget, int textureWidth, int textureHeight, EnumPixelFormat internalFormat) {
		super(texTarget);
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.pixelFormat = internalFormat;
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
		}
	}
	
	@Override
	protected void subbindTexture() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
	}

	@Override
	public boolean attach(FrameBufferAttachment attachment) {
		if(textureId == 0) return false;
		ARBFramebufferObject.glFramebufferTexture2D(ARBFramebufferObject.GL_FRAMEBUFFER, 
				attachment.getFrameBufferAttachment(), textureType.getValue(), textureId, 0);
		return true;
	}

	@Override
	public boolean detach(FrameBufferAttachment attachment) {
		ARBFramebufferObject.glFramebufferTexture2D(ARBFramebufferObject.GL_FRAMEBUFFER, 
				attachment.getFrameBufferAttachment(), textureType.getValue(), 0, 0);
		return true;
	}
}
