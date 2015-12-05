package net.aegistudio.transparent.texture;

import java.util.Stack;

import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import net.aegistudio.transparent.FeatureUnsupportedException;
import net.aegistudio.transparent.model.Effect;

public abstract class TextureBinding implements Effect {
	protected final Texture targetTexture;
	
	//Whether the texture will be created or removed the same time as the effect.
	protected final boolean scoped;	
	
	protected TextureBinding(Texture targetTexture, boolean scoped) {
		this.targetTexture = targetTexture;
		this.scoped = scoped;
	}
	
	protected static int maxMultiTextureUnit = -1;
	protected static Stack<Texture>[] multiTextureAllocationList;
	protected static Texture[] currentMultiTexture;
	
	public void create() {
		if(scoped) targetTexture.create();
	}
	
	public void destroy() {
		if(scoped) targetTexture.destroy();
	}
	
	public abstract int getCurrentBinding();
	
	protected void bindMultitexture(int textureUnit) {
		this.switchMultitextureTarget(textureUnit);
		this.targetTexture.bind();
		
		if(currentMultiTexture[textureUnit] == null)
			GL11.glEnable(targetTexture.getTextureType().getValue());

		multiTextureAllocationList[textureUnit]
				.push(currentMultiTexture[textureUnit]);
		currentMultiTexture[textureUnit] = targetTexture;
	}
	
	protected void unbindMultitexture(int textureUnit) {
		this.switchMultitextureTarget(textureUnit);
		Texture texture = multiTextureAllocationList[textureUnit].pop();
		currentMultiTexture[textureUnit] = texture;
		
		if(texture != null)
			texture.bind();
		else
			GL11.glDisable(targetTexture.getTextureType().getValue());	
	}
	
	private void switchMultitextureTarget(int textureUnit) {
		multiTextureEnvironmentInit();
		if(maxMultiTextureUnit > 0)
			ARBMultitexture.glActiveTextureARB(
					ARBMultitexture.GL_TEXTURE0_ARB + textureUnit);
		else if(textureUnit == 0 && textureUnit > 0)
			throw new FeatureUnsupportedException("multi-texture");
		else if(textureUnit >= maxMultiTextureUnit)
			throw new FeatureUnsupportedException(
					String.format("multi-texture unit #%d", textureUnit));
	}

	@SuppressWarnings("unchecked")
	private static void multiTextureEnvironmentInit() {
		if(maxMultiTextureUnit < 0) {
			if(GLContext.getCapabilities().GL_ARB_multitexture) {
				maxMultiTextureUnit = GL11.glGetInteger(ARBMultitexture.GL_MAX_TEXTURE_UNITS_ARB);
				multiTextureAllocationList = new Stack[maxMultiTextureUnit];
				currentMultiTexture = new Texture[maxMultiTextureUnit];
				
				for(int i = 0; i < maxMultiTextureUnit; i ++)
					multiTextureAllocationList[i] = new Stack<Texture>();
			}
			else {
				maxMultiTextureUnit = 0;
				multiTextureAllocationList = new Stack[1];
				multiTextureAllocationList[0] = new Stack<Texture>();
				currentMultiTexture = new Texture[1];
			}
		}
	}
}
