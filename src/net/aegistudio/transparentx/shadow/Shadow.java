package net.aegistudio.transparentx.shadow;

import org.lwjgl.opengl.GL11;

import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.combine.Combine;
import net.aegistudio.transparentx.combine.ModifyReplaceStreamed;

public class Shadow implements ShaderEffectClass {
	
	public static final int DIRECTIONAL = 997;
	public static final int POINT = 998;
	public static final int SPOTLIGHT = 999;
	
	private final int basePriority, targetLightSource;
	
	Shadow(int basePriority, int targetLightSource) {
		this.basePriority = basePriority;
		this.targetLightSource = targetLightSource;
	}
	
	@Override
	public double getPriority() {
		int maxLights = GL11.glGetInteger(GL11.GL_MAX_LIGHTS);
		return this.basePriority + (this.targetLightSource * 1.0) / maxLights;
	}

	@Override
	public Combine getCombine(String mutatedVariable) {
		return new ModifyReplaceStreamed(0);
	}
}
