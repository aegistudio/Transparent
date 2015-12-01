package net.aegistudio.transparentx.map;

import net.aegistudio.transparent.hint.EnumBlendMethod;
import net.aegistudio.transparent.shader.EnumShaderData;
import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparentx.ShaderEffect;
import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.ShaderResource;
import net.aegistudio.transparentx.ShaderUniform;
import net.aegistudio.transparentx.combine.ModifyBlendOriginal;

public abstract class ColorMapping implements ShaderEffect {
	private final int texTarget;
	private final TextureMapping mapping;
	private final ShaderResource color_mapping_vsh = new ShaderResource("color_mapping.fsh"){};
	private ShaderUniform uniform = new ShaderUniform(this, EnumShaderData.TEXTURE, "texTarget");
	
	public ColorMapping(int texTarget, int order, EnumBlendMethod blendMethod) {
		this.texTarget = texTarget;
		this.mapping = new TextureMapping("gl_FragColor", order, new ModifyBlendOriginal(blendMethod));
	}
	
	public ColorMapping(int texTarget) {
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
		if(shaderType == EnumShaderType.FRAGMENT) return new String[]{color_mapping_vsh.getResource()
				.replaceAll("%srcCoord", Integer.toString(texTarget))};
		return null;
	}

	@Override
	public void setParameters() {
		uniform.set(texTarget);
		uniform.use();
	}
}
