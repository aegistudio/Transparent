package net.aegistudio.transparentx.glow;

import net.aegistudio.transparent.hint.EnumBlendMethod;
import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparentx.ShaderResource;

public class RestreamedColor extends GlowingSubEffect{
	public RestreamedColor(double insertPoint) {
		super(new GlowingPreprocessor(insertPoint, EnumBlendMethod.ACCUMULATE));
	}

	ShaderResource restreamed_color_fsh = new ShaderResource("restreamed_color.fsh"){};
	
	@Override
	public String[] getGlowingRenderSource(EnumShaderType shaderType) {
		if(shaderType == EnumShaderType.FRAGMENT) return new String[] {restreamed_color_fsh.getResource()};
		else return null;
	}

	@Override
	public void setGlowingParameter() {
	
	}
}
