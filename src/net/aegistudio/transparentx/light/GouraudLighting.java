package net.aegistudio.transparentx.light;

import net.aegistudio.transparent.hint.EnumBlendMethod;
import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparentx.ShaderEffect;
import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.ShaderResource;
import net.aegistudio.transparentx.combine.Combine;
import net.aegistudio.transparentx.combine.ModifyBlendOriginal;

/**
 * Gouraud shading get lighting parameters from OpenGL
 * lighting settings. Calculate lighting intensity for every
 * vertices and interpolates them.
 * 
 * @author aegistudio
 */

public class GouraudLighting implements ShaderEffect {
	protected ShaderResource lighting_vsh = new ShaderResource("gouraud.vsh"){};
	
	private final Combine colorBlending;
	public GouraudLighting(Combine colorBlending) {
		this.colorBlending = colorBlending;
	}
	
	public GouraudLighting() {
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
		else return null;
	}

	@Override
	public void setParameters() {	}
}
