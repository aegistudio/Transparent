package net.aegistudio.transparentx.map;

import net.aegistudio.transparent.shader.EnumShaderData;
import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparentx.ShaderEffect;
import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.ShaderResource;
import net.aegistudio.transparentx.ShaderUniform;
import net.aegistudio.transparentx.combine.ModifyReplaceOriginal;

public abstract class BumpMapping implements ShaderEffect {
	private final int texTarget;
	private final TextureMapping mapping;
	private final ShaderResource bump_mapping_vsh = new ShaderResource("bump_mapping.fsh"){};
	private ShaderUniform uniform = new ShaderUniform(this, EnumShaderData.TEXTURE, "texTarget");
	private ShaderUniform modulator = new ShaderUniform(this, EnumShaderData.VEC4, "modulator");
	private ShaderUniform dx = new ShaderUniform(this, EnumShaderData.VEC2, "dx");
	private ShaderUniform dy = new ShaderUniform(this, EnumShaderData.VEC2, "dy");
	private ShaderUniform heightOffset = new ShaderUniform(this, EnumShaderData.FLOAT, "heightOffset");{
		modulator.set(.05f, .05f, .05f, 0f);
		dx.set(.01f, .0f); dy.set(.0f, .01f); heightOffset.set(0f);
	};
	
	public BumpMapping(int texTarget, int order) {
		this.texTarget = texTarget;
		this.mapping = new TextureMapping(order, new ModifyReplaceOriginal(), "_viewVector", "_normal"){};
	}
	
	public BumpMapping(int texTarget) {
		this(texTarget, texTarget);
	}
	
	@Override
	public ShaderEffectClass getShaderEffectClass() {
		return mapping;
	}
	
	@Override
	public String[] getRenderSource(EnumShaderType shaderType) {
		if(shaderType == EnumShaderType.FRAGMENT) 
			return new String[]{bump_mapping_vsh.getResource()
				.replaceAll("%srcCoord", Integer.toString(texTarget))};
		return null;
	}

	@Override
	public void setParameters() {
		uniform.set(texTarget);
		uniform.use();
		modulator.use();
		dx.use(); dy.use();
		heightOffset.use();
	}
	
	public void setModulator(float r, float g, float b, float a) {
		this.modulator.set(r, g, b, a);
	}
	
	public void setSamplingVector(float dx_x, float dx_y, float dy_x, float dy_y) {
		this.dx.set(dx_x, dx_y);
		this.dy.set(dy_x, dy_y);
	}
	
	public void setHeightOffset(float hoff) {
		this.heightOffset.set(hoff);
	}
}
