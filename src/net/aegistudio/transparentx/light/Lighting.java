package net.aegistudio.transparentx.light;

import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.combine.Combine;
import net.aegistudio.transparentx.combine.ModifyReplaceOriginal;
import net.aegistudio.transparentx.combine.ModifyReplaceStreamed;
import net.aegistudio.transparentx.combine.NomodifyRedundant;
import net.aegistudio.transparentx.lang.SharingVariable;

public class Lighting implements ShaderEffectClass{

	public final Combine colorBlending;
	public Lighting(Combine colorBlending) {
		this.colorBlending = colorBlending;
		new SharingVariable("_normal_interpolate", "vec3", null).submit();
		new SharingVariable("_viewVector_interpolate", "vec4", null).submit();
		new SharingVariable("_normal", "vec3", EnumShaderType.FRAGMENT, 
				"_normal = _normal_interpolate;").submit();
		new SharingVariable("_viewVector", "vec4", EnumShaderType.FRAGMENT,
				"_viewVector = _viewVector_interpolate;").submit();
		
		new SharingVariable("_ambient_response", "vec4", EnumShaderType.FRAGMENT,
				"_ambient_response = gl_FrontMaterial.ambient;").submit();
		new SharingVariable("_diffuse_response", "vec4", EnumShaderType.FRAGMENT,
				"_diffuse_response = gl_FrontMaterial.diffuse;").submit();
		new SharingVariable("_specular_response", "vec4", EnumShaderType.FRAGMENT,
				"_specular_response = gl_FrontMaterial.specular;").submit();
		
		new SharingVariable("_emission", "vec4", EnumShaderType.FRAGMENT,
				"_emission = gl_FrontMaterial.emission;").submit();
		new SharingVariable("_shininess", "float", EnumShaderType.FRAGMENT,
				"_shininess= gl_FrontMaterial.shininess;").submit();
		new SharingVariable("_shadow", "float", EnumShaderType.FRAGMENT,
				"int _shadowinit; for(_shadowinit=0; _shadowinit < gl_MaxLights; _shadowinit++) _shadow[_shadowinit]=1.0;").submit();
	}
	
	/**
	 * Lightings always modulate the color of others, so it should 
	 * come out as late as possible.
	 */
	@Override
	public double getPriority() {
		return 1000;
	}

	@Override
	public Combine getCombine(String mutatedVariable) {
		switch(mutatedVariable) {
			case "gl_Position":
				return new NomodifyRedundant(1);
				
			case "_viewVector_interpolate":
			case "_normal_interpolate":
				return new ModifyReplaceOriginal();
				
			case "_viewVector":
			case "_normal":
			case "_ambient_response":
			case "_diffuse_response":
			case "_specular_response":
			case "_emission":
			case "_shininess":
			case "_shadow":
				return new ModifyReplaceStreamed();
				
			case "gl_FrontColor":
			case "gl_BackColor":
			case "gl_FragColor":
				return colorBlending;
		}
		return null;
	}

}
