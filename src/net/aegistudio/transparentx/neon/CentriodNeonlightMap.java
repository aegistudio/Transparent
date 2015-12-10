package net.aegistudio.transparentx.neon;

import net.aegistudio.transparent.shader.EnumShaderData;
import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparentx.ShaderEffect;
import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.ShaderResource;
import net.aegistudio.transparentx.ShaderUniform;
import net.aegistudio.transparentx.combine.ModifyReplaceStreamed;
import net.aegistudio.transparentx.map.TextureMapping;

public class CentriodNeonlightMap implements ShaderEffect{
	private int texTarget;
	private TextureMapping texMap;
	public CentriodNeonlightMap(int texTarget, int order) {
		this.texTarget = texTarget;
		this.texMap = new TextureMapping("gl_FragColor", texTarget, new ModifyReplaceStreamed(0));
		
		this.setComponentFactor(0.3f, 0.3f, 0.4f, 0);
		
		this.setCentriod(0.1f, 0.02f, 0.15f);
		this.setDirection(2);
	}
	
	@Override
	public ShaderEffectClass getShaderEffectClass() {
		return texMap;
	}

	ShaderResource centriod_neonlight_fsh = new ShaderResource("centriod_neonlight.fsh"){};
	@Override
	public String[] getRenderSource(EnumShaderType shaderType) {
		if(shaderType == EnumShaderType.FRAGMENT) return new String[] {centriod_neonlight_fsh.getResource().
				replaceAll("%srcCoord", Integer.toString(texTarget))};
		return null;
	}
	
	private ShaderUniform texTargetUniform = new ShaderUniform(this, EnumShaderData.TEXTURE, "texTarget");
	private ShaderUniform componentFactor = new ShaderUniform(this, EnumShaderData.VEC4, "componentFactor");
	
	private ShaderUniform radium = new ShaderUniform(this, EnumShaderData.FLOAT, "radium");
	private ShaderUniform step = new ShaderUniform(this, EnumShaderData.FLOAT, "step");
	private ShaderUniform significance = new ShaderUniform(this, EnumShaderData.FLOAT, "significance");
	private ShaderUniform direction = new ShaderUniform(this, EnumShaderData.INT, "direction");
	
	@Override
	public void setParameters() {
		texTargetUniform.set(texTarget);
		texTargetUniform.use();
		componentFactor.use();
		radium.use();
		step.use();
		significance.use();
		direction.use();
	}
	
	public void setComponentFactor(float r, float g, float b, float a) {
		componentFactor.set(r, g, b, a);
	}
	
	public void setCentriod(float radium, float step, float significance) {
		this.radium.set(radium);
		this.step.set(step);
		this.significance.set(significance);
	}
	
	public void setDirection(int count) {
		this.direction.set(count);
	}
}
