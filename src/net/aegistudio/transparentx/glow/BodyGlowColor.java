package net.aegistudio.transparentx.glow;

import net.aegistudio.transparent.hint.EnumBlendMethod;
import net.aegistudio.transparent.shader.EnumShaderData;
import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparentx.ShaderResource;
import net.aegistudio.transparentx.ShaderUniform;

public class BodyGlowColor extends GlowingSubEffect{
	public BodyGlowColor() {
		this(EnumBlendMethod.ACCUMULATE);
	}
	
	public BodyGlowColor(EnumBlendMethod method) {
		super(new GlowingPreprocessor(8, method));
	}

	ShaderResource body_glow_fsh = new ShaderResource("body_glow_color.fsh"){};
	ShaderResource body_glow_vsh = new ShaderResource("body_glow_color.vsh"){};
	ShaderUniform bodyGlowColor = new ShaderUniform(this, EnumShaderData.VEC4, "glowColor"); {
		this.setBodyGlowColor(1, 1, 1);
	}
	
	/**
	 * Set the glow color of a glow body.
	 */
	
	public void setBodyGlowColor(float r, float g, float b) {
		bodyGlowColor.set(r, g, b, 1.0f);
	}
	
	@Override
	public String[] getGlowingRenderSource(EnumShaderType shaderType) {
		if(shaderType == EnumShaderType.VERTEX) return new String[] {body_glow_vsh.getResource()};
		else if(shaderType == EnumShaderType.FRAGMENT) return new String[] {body_glow_fsh.getResource()};
		else return null;
	}

	@Override
	public void setGlowingParameter() {
		bodyGlowColor.use();
	}
}
