package net.aegistudio.transparent.texture;

import org.lwjgl.opengl.GL11;
import net.aegistudio.transparent.UninitializedException;

/**
 * Generalize textures (including multitexture) in
 * OpenGL. Please notice the differences between different
 * multitexture binding methods.
 * 
 * @author aegistudio
 */

public abstract class Texture {
	protected int textureId;
	protected EnumTexture textureType;
	
	public Texture(EnumTexture textureType) {
		this.textureId = 0;
		this.textureType = textureType;
	}
	
	public abstract void create(int mipmapLevel);
	
	public void create() {
		this.create(0);
	}
	
	EnumTexture getTextureType() {
		return this.textureType;
	}
	
	public void bind() {
		if(textureId == 0) throw new UninitializedException(this);
		this.setupTextureEnvironment();
		this.subbindTexture();
	}
	
	protected abstract void subbindTexture();
	
	public void destroy() {
		if(textureId == 0) return;
		GL11.glDeleteTextures(textureId);
		textureId = 0;
	}
	
	protected void setupTextureEnvironment() {
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, 
				org.lwjgl.opengl.ARBTextureEnvCombine.GL_COMBINE_ARB);
	}
	
	protected void setupTextureParameter() {
		GL11.glTexParameteri(textureType.getValue(), GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(textureType.getValue(), GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glTexParameteri(textureType.getValue(), GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(textureType.getValue(), GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
	}
}
