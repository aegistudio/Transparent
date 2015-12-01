package net.aegistudio.transparentx.map;

import net.aegistudio.transparent.hint.EnumBlendMethod;
import net.aegistudio.transparent.shader.EnumShaderData;
import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparentx.ShaderEffect;
import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.ShaderResource;
import net.aegistudio.transparentx.ShaderUniform;
import net.aegistudio.transparentx.combine.ModifyBlendOriginal;

public class PixelMapping implements ShaderEffect {
	
	private final int texTarget;
	private final TextureMapping mapping;
	private final ShaderResource pixel_mapping_vsh = new ShaderResource("pixel_mapping.fsh"){};
	private ShaderUniform texTargetUniform = new ShaderUniform(this, EnumShaderData.TEXTURE, "texTarget");
	private ShaderUniform distortionFactorUniform = new ShaderUniform(this, EnumShaderData.FLOAT, "distortionFactor");{
		distortionFactorUniform.set(0.1f);
	};
	
	public PixelMapping(int texTarget, int order, EnumBlendMethod blendMethod) {
		this.texTarget = texTarget;
		this.mapping = new TextureMapping("gl_FragColor", order, new ModifyBlendOriginal(blendMethod));
	}
	
	public PixelMapping(int texTarget) {
		if(texTarget == 0) mapping = new TextureMapping("gl_FragColor", 
				texTarget, new ModifyBlendOriginal(EnumBlendMethod.MODULATE));
		else mapping = new TextureMapping("gl_FragColor", texTarget, 
				new ModifyBlendOriginal(EnumBlendMethod.DECALATE));
		this.texTarget = texTarget;
	}

	@Override
	public ShaderEffectClass getShaderEffectClass() {
		return mapping;
	}

	@Override
	public String[] getRenderSource(EnumShaderType shaderType) {
		if(shaderType == EnumShaderType.FRAGMENT) return new String[]{pixel_mapping_vsh.getResource()
				.replaceAll("%srcCoord", Integer.toString(texTarget))};
		return null;
	}

	@Override
	public void setParameters() {
		texTargetUniform.set(texTarget);
		texTargetUniform.use();
		distortionFactorUniform.use();
	}
	
	public void setDistortionFactor(float factor) {
		distortionFactorUniform.set(factor);
	}
}
