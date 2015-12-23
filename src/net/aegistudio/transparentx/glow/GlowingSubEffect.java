package net.aegistudio.transparentx.glow;

import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparentx.ShaderEffect;

public abstract class GlowingSubEffect implements ShaderEffect{
	private final GlowingPreprocessor preprocessor;
	
	public GlowingSubEffect(GlowingPreprocessor preprocessor) {
		this.preprocessor = preprocessor;
	}

	private boolean inStrip = false;
	public void setGlowingStrip(boolean inStrip) {
		this.inStrip = inStrip;
	}
	
	@Override
	public String[] getRenderSource(EnumShaderType shaderType) {
		if(inStrip) return this.getGlowingRenderSource(shaderType);
		else return null;
	}
	
	public void setParameters() {
		this.setGlowingParameter();
	}
	
	public GlowingPreprocessor getShaderEffectClass() {
		return this.preprocessor;
	}
	
	public abstract String[] getGlowingRenderSource(EnumShaderType shaderType);
	public abstract void setGlowingParameter();
}
