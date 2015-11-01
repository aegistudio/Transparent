package net.aegistudio.transparentx.light;

import net.aegistudio.transparent.hint.EnumBlendMethod;
import net.aegistudio.transparent.model.Drawable;
import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparentx.ShaderEffect;
import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.ShaderResource;
import net.aegistudio.transparentx.combine.Combine;
import net.aegistudio.transparentx.combine.ModifyBlendOriginal;

public class PixelLighting implements ShaderEffect {
	protected ShaderResource perpixel_lighting_vsh = new ShaderResource("perpixel_lighting.vsh"){};
	protected ShaderResource perpixel_lighting_fsh = new ShaderResource("perpixel_lighting.fsh"){};
	
	private final Combine colorBlending;
	public PixelLighting(Combine colorBlending) {
		this.colorBlending = colorBlending;
	}
	
	public PixelLighting() {
		this(new ModifyBlendOriginal(EnumBlendMethod.MODULATE));
	}
	
	@Override
	public ShaderEffectClass getShaderEffectClass() {
		return new Lighting(colorBlending);
	}

	@Override
	public boolean shouldPrerender() {
		return false;
	}

	@Override
	public void doPrerender(Drawable prerendering) {	}

	@Override
	public String[] getRenderSource(EnumShaderType shaderType) {
		if(shaderType == EnumShaderType.VERTEX)
			return new String[]{perpixel_lighting_vsh.getResource()};
		else if(shaderType == EnumShaderType.FRAGMENT)
			return new String[]{perpixel_lighting_fsh.getResource()};
		else return null;
	}

	@Override
	public void setParameters() {	}
}
