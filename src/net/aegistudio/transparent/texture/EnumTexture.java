package net.aegistudio.transparent.texture;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

import net.aegistudio.transparent.Constant;

public enum EnumTexture implements Constant{
	LINEAR(GL11.GL_TEXTURE_1D),
	SURFACE(GL11.GL_TEXTURE_2D),
	BODY(GL12.GL_TEXTURE_3D),
	CUBIC(GL13.GL_TEXTURE_CUBE_MAP);
	
	private final int textureId;
	
	private EnumTexture(int textureId) {
		this.textureId = textureId;
	}

	@Override
	public int getValue() {
		return this.textureId;
	}
	
}
