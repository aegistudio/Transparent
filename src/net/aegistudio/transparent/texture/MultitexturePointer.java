package net.aegistudio.transparent.texture;

import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.GLContext;

import net.aegistudio.transparent.vao.ArrayPointer;
import net.aegistudio.transparent.vao.EnumArrayPointer;

public class MultitexturePointer implements ArrayPointer {
	private final int multitextureTarget;
	
	public MultitexturePointer(int targetTexture) {
		this.multitextureTarget = targetTexture;
	}
	
	@Override
	public void enable() {
		if(GLContext.getCapabilities().GL_ARB_multitexture) {
			ARBMultitexture.glClientActiveTextureARB(ARBMultitexture.GL_TEXTURE0_ARB
					+ multitextureTarget);
		}
		EnumArrayPointer.TEXTURE.enable();
	}

	@Override
	public void arrayPointer(int size, int type, int stride, long offset) {
		if(GLContext.getCapabilities().GL_ARB_multitexture) {
			ARBMultitexture.glClientActiveTextureARB(ARBMultitexture.GL_TEXTURE0_ARB
					+ multitextureTarget);
		}
		EnumArrayPointer.TEXTURE.arrayPointer(size, type, stride, offset);
	}

	@Override
	public void disable() {
		if(GLContext.getCapabilities().GL_ARB_multitexture) {
			ARBMultitexture.glClientActiveTextureARB(ARBMultitexture.GL_TEXTURE0_ARB
				+ multitextureTarget);
		}
		EnumArrayPointer.TEXTURE.disable();
	}

	@Override
	public int getDefaultSize() {
		return 2;
	}

	@Override
	public void draw() {
		
	}
}
