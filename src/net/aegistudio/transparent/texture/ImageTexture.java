package net.aegistudio.transparent.texture;

import org.lwjgl.opengl.GL11;

import net.aegistudio.transparent.UnallocatableException;
import net.aegistudio.transparent.image.EnumPixelFormat;
import net.aegistudio.transparent.image.Image;

public class ImageTexture extends Texture {

	private final Image image;
	private final EnumPixelFormat internalFormat;
	
	public ImageTexture(Image image, EnumPixelFormat internalFormat) {
		super(EnumTexture.SURFACE);
		this.image = image;
		this.internalFormat = internalFormat;
	}
	
	public ImageTexture(Image image) {
		this(image, image.getPixelFormat());
	}
	
	protected void subbindTexture() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
	}
	
	@Override
	public void create(int mipmapLevel) {
		if(this.textureId == 0) {
			this.textureId = GL11.glGenTextures();
			if(this.textureId == 0) throw new UnallocatableException(this);
			
			int previousTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
			this.setupTextureParameter();
			image.textureImage2D(GL11.GL_TEXTURE_2D, mipmapLevel, internalFormat, 0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousTexture);
		}
	}	
}
