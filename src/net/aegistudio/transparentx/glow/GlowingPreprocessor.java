package net.aegistudio.transparentx.glow;

import net.aegistudio.transparent.hint.EnumBlendMethod;
import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.combine.Combine;
import net.aegistudio.transparentx.combine.ModifyBlendOriginal;
import net.aegistudio.transparentx.combine.ModifyReplaceOriginal;
import net.aegistudio.transparentx.combine.NomodifyRedundant;

public class GlowingPreprocessor implements ShaderEffectClass {
	private final Combine combine;
	private final double priority;
	
	public GlowingPreprocessor(double priority) {
		this.priority = priority;
		this.combine = new ModifyReplaceOriginal();
	}
	
	public GlowingPreprocessor(double priority, EnumBlendMethod blending) {
		this.priority = priority;
		this.combine = new ModifyBlendOriginal(blending);
	}

	@Override
	public double getPriority() {
		return priority;
	}

	@Override
	public Combine getCombine(String mutatedVariable) {
		if(mutatedVariable.equals("_glowingMapColor")) return combine;
		else if(mutatedVariable.equals("gl_Position")) return new NomodifyRedundant(1);
		else return null;
	}
}
