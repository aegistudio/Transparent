package net.aegistudio.transparentx.map;

import net.aegistudio.transparent.model.Drawable;
import net.aegistudio.transparent.shader.EnumShaderData;
import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparentx.ShaderEffect;
import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.ShaderResource;
import net.aegistudio.transparentx.ShaderUniform;
import net.aegistudio.transparentx.combine.ModifyReplaceOriginal;

public abstract class NormalMapping implements ShaderEffect {
	private final int texTarget;
	private final TextureMapping mapping;
	private final ShaderResource normal_mapping_vsh = new ShaderResource("normal_mapping.fsh"){};
	private ShaderUniform uniform = new ShaderUniform(this, EnumShaderData.TEXTURE, "texTarget");
	
	public NormalMapping(int texTarget, int order) {
		this.texTarget = texTarget;
		this.mapping = new TextureMapping("_normal", order, new ModifyReplaceOriginal());
	}
	
	public NormalMapping(int texTarget) {
		this(texTarget, texTarget);
	}
	
	@Override
	public ShaderEffectClass getShaderEffectClass() {
		return mapping;
	}

	@Override
	public boolean shouldPrerender() {
		return false;
	}

	@Override
	public void doPrerender(Drawable prerendering) {	}

	@Override
	public String[] getRenderSource(EnumShaderType shaderType) {
		if(shaderType == EnumShaderType.FRAGMENT) 
			return new String[]{normal_mapping_vsh.getResource()
				.replaceAll("%srcCoord", Integer.toString(texTarget))};
		return null;
	}

	@Override
	public void setParameters() {
		uniform.set(texTarget);
		uniform.use();
	}
}
