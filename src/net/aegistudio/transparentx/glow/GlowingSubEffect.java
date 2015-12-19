package net.aegistudio.transparentx.glow;

import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparentx.ShaderEffect;
import net.aegistudio.transparentx.ShaderEffectClass;

public abstract class GlowingSubEffect implements ShaderEffect{
	@Override
	public abstract ShaderEffectClass getShaderEffectClass();

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
	
	public abstract String[] getGlowingRenderSource(EnumShaderType shaderType);
	public abstract void setGlowingParameter();
}
