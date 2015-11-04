package net.aegistudio.transparentx.map;

import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.GL11;

import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.combine.Combine;
import net.aegistudio.transparentx.combine.NomodifyOriginal;

/**
 * Texture coordinating will exclusively override a multi-texture
 * target with given texture coordinating.
 * Priority will be between 0.0(inclusive)~1.0(exclusive).
 * 
 * @author aegistudio
 */

public class TextureCoordinating implements ShaderEffectClass {
	
	private final int multiTexTarget;
	
	public TextureCoordinating(int multiTexTarget) {
		this.multiTexTarget = multiTexTarget;
	}
	
	@Override
	public double getPriority() {
		int maxUnits = GL11.glGetInteger(ARBMultitexture.GL_MAX_TEXTURE_UNITS_ARB);
		return (1.0 * multiTexTarget) / maxUnits;
	}

	@Override
	public Combine getCombine(String mutatedVariable) {
		return new NomodifyOriginal();
	}
}
