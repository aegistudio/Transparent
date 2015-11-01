package net.aegistudio.transparentx.light;

import net.aegistudio.transparent.hint.EnumBlendMethod;
import net.aegistudio.transparent.model.Drawable;
import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparentx.ShaderEffect;
import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.ShaderResource;
import net.aegistudio.transparentx.combine.Combine;
import net.aegistudio.transparentx.combine.ModifyBlendOriginal;

/**
 * This shader program get lighting parameters from OpenGL
 * lighting settings. Which means render by vertices.
 * 
 * @author aegistudio
 */

public class VertexLighting implements ShaderEffect {
	protected ShaderResource pervert_lighting_vsh = new ShaderResource("pervert_lighting.vsh"){};
	
	private final Combine colorBlending;
	public VertexLighting(Combine colorBlending) {
		this.colorBlending = colorBlending;
	}
	
	public VertexLighting() {
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
			return new String[]{pervert_lighting_vsh.getResource()};
		else return null;
	}

	@Override
	public void setParameters() {	}
}
