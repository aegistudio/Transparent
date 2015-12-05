package net.aegistudio.transparentx.shadow;

import net.aegistudio.transparent.shader.EnumShaderData;
import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparentx.ShaderEffect;
import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.ShaderResource;
import net.aegistudio.transparentx.ShaderUniform;
import net.aegistudio.transparentx.combine.Combine;
import net.aegistudio.transparentx.combine.ModifyReplaceStreamed;

public class Fog implements ShaderEffect {
	
	static final ShaderEffectClass shClass = new ShaderEffectClass() {
		@Override
		public double getPriority() {
			return 20;
		}

		@Override
		public Combine getCombine(String mutatedVariable) {
			return new ModifyReplaceStreamed(0);
		}
	};

	@Override
	public ShaderEffectClass getShaderEffectClass() {
		return shClass;
	}

	ShaderUniform x0 = new ShaderUniform(this, EnumShaderData.FLOAT, "x0"); 
	ShaderUniform x1 = new ShaderUniform(this, EnumShaderData.FLOAT, "x1");
	ShaderUniform x2 = new ShaderUniform(this, EnumShaderData.FLOAT, "x2");
	ShaderUniform threshold = new ShaderUniform(this, EnumShaderData.FLOAT, "threshold");
	ShaderUniform regulate = new ShaderUniform(this, EnumShaderData.FLOAT, "regulate");
	
	ShaderResource fog_vsh = new ShaderResource("fog.vsh"){};
	ShaderResource fog_fsh = new ShaderResource("fog.fsh"){};
	
	public Fog() {
		x0.set(1f);
		x1.set(0f);
		x2.set(0f);
		threshold.set(0.1f);
	}
	
	@Override
	public String[] getRenderSource(EnumShaderType shaderType) {
		if(shaderType == EnumShaderType.VERTEX) return new String[]{fog_vsh.getResource()};
		else if(shaderType == EnumShaderType.FRAGMENT) return new String[]{fog_fsh.getResource()};
		else return null;
	}

	@Override
	public void setParameters() {
		x0.use();
		x1.use();
		x2.use();
		threshold.use();
	}
	
	public void setAttenuation(float x0, float x1, float x2) {
		this.x0.set(x0);
		this.x1.set(x1);
		this.x2.set(x2);
	}
	
	public void setThreshold(float threshold) {
		this.threshold.set(threshold);
	}
}
