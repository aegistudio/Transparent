package net.aegistudio.transparentx.glow;

import net.aegistudio.transparent.shader.EnumShaderData;
import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.ShaderResource;
import net.aegistudio.transparentx.ShaderUniform;
import net.aegistudio.transparentx.combine.Combine;
import net.aegistudio.transparentx.combine.ModifyReplaceOriginal;
import net.aegistudio.transparentx.combine.NomodifyRedundant;

public class BodyGlowColor extends GlowingSubEffect{

	@Override
	public ShaderEffectClass getShaderEffectClass() {
		return new ShaderEffectClass() {
			@Override
			public double getPriority() {
				return 8;
			}

			@Override
			public Combine getCombine(String mutatedVariable) {
				if(mutatedVariable.equals("_glowingMapColor")) return new ModifyReplaceOriginal();
				else if(mutatedVariable.equals("gl_Position")) return new NomodifyRedundant(1);
				else return null;
			}
		};
	}

	ShaderResource body_glow_fsh = new ShaderResource("body_glow_color.fsh"){};
	ShaderResource body_glow_vsh = new ShaderResource("body_glow_color.vsh"){};
	ShaderUniform bodyGlowColor = new ShaderUniform(this, EnumShaderData.VEC4, "glowColor"); {
		this.setBodyGlowColor(1, 1, 1);
	}
	
	public void setBodyGlowColor(float r, float g, float b) {
		bodyGlowColor.set(r, g, b, 1.0f);
	}
	
	@Override
	public String[] getGlowingRenderSource(EnumShaderType shaderType) {
		if(shaderType == EnumShaderType.VERTEX) return new String[] {body_glow_vsh.getResource()};
		else if(shaderType == EnumShaderType.FRAGMENT) return new String[] {body_glow_fsh.getResource()};
		else return null;
	}

	@Override
	public void setGlowingParameter() {
		bodyGlowColor.use();
	}
}
