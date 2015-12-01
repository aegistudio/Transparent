package net.aegistudio.transparentx.light;

import net.aegistudio.transparent.hint.EnumBlendMethod;

import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparentx.ShaderEffect;
import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.ShaderResource;
import net.aegistudio.transparentx.combine.Combine;
import net.aegistudio.transparentx.combine.ModifyBlendOriginal;

/**
 * Blinn-Phong shading get lighting parameters from OpenGL
 * lighting settings. Calculate normal vector for every
 * vertices and calculate light intensity for every pixels
 * in fragment.
 * 
 * @author aegistudio
 */

public class PhongLighting implements ShaderEffect {
	protected ShaderResource lighting_vsh = new ShaderResource("blinn_phong.vsh"){};
	protected ShaderResource lighting_fsh = new ShaderResource("blinn_phong.fsh"){};
	
	private final Combine colorBlending;
	public PhongLighting(Combine colorBlending) {
		this.colorBlending = colorBlending;
	}
	
	public PhongLighting() {
		this(new ModifyBlendOriginal(EnumBlendMethod.MODULATE));
	}
	
	@Override
	public ShaderEffectClass getShaderEffectClass() {
		return new Lighting(colorBlending);
	}

	@Override
	public String[] getRenderSource(EnumShaderType shaderType) {
		if(shaderType == EnumShaderType.VERTEX)
			return new String[]{lighting_vsh.getResource()};
		else if(shaderType == EnumShaderType.FRAGMENT)
			return new String[]{lighting_fsh.getResource()};
		else return null;
	}
	
	@Override
	public void setParameters() {	}
}
