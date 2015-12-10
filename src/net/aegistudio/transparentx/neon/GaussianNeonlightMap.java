package net.aegistudio.transparentx.neon;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import net.aegistudio.transparent.shader.EnumShaderData;
import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparentx.ShaderEffect;
import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.ShaderResource;
import net.aegistudio.transparentx.ShaderUniform;
import net.aegistudio.transparentx.combine.ModifyReplaceStreamed;
import net.aegistudio.transparentx.map.TextureMapping;

public class GaussianNeonlightMap implements ShaderEffect {
	private int texTarget;
	private TextureMapping texMap;
	public GaussianNeonlightMap(int texTarget, int order) {
		this.texTarget = texTarget;
		this.texMap = new TextureMapping("gl_FragColor", order, new ModifyReplaceStreamed(0));
		
		this.setComponentFactor(0.3f, 0.3f, 0.4f, 0);
		this.setBiasVector(0.01f, 0.01f);
		
		gaussianBaseBuffer = BufferUtils.createFloatBuffer(9);
		this.setGausianBase(new float[] {
				0.1f, 0.2f, 0.1f,
				0.2f, 0.4f, 0.1f,
				0.1f, 0.2f, 0.1f
		});
		this.setSignificance(0.15f);
	}
	
	@Override
	public ShaderEffectClass getShaderEffectClass() {
		return texMap;
	}

	ShaderResource gaussian_neonlight_fsh = new ShaderResource("gaussian_neonlight.fsh"){};
	@Override
	public String[] getRenderSource(EnumShaderType shaderType) {
		if(shaderType == EnumShaderType.FRAGMENT) return new String[] {gaussian_neonlight_fsh.getResource().
				replaceAll("%srcCoord", Integer.toString(texTarget))};
		return null;
	}
	
	private ShaderUniform texTargetUniform = new ShaderUniform(this, EnumShaderData.TEXTURE, "texTarget");
	private ShaderUniform componentFactor = new ShaderUniform(this, EnumShaderData.VEC4, "componentFactor");
	private ShaderUniform bias = new ShaderUniform(this, EnumShaderData.VEC2, "bias");
	private ShaderUniform gaussianBase = new ShaderUniform(this, EnumShaderData.MATRIX3, "gaussianBase");
	private ShaderUniform significance = new ShaderUniform(this, EnumShaderData.FLOAT, "significance");
	
	@Override
	public void setParameters() {
		texTargetUniform.set(texTarget);
		texTargetUniform.use();
		componentFactor.use();
		bias.use();
		gaussianBase.use();
		significance.use();
	}

	public void setComponentFactor(float r, float g, float b, float a) {
		componentFactor.set(r, g, b, a);
	}
	
	public void setBiasVector(float x, float y) {
		bias.set(x, y);
	}
	
	FloatBuffer gaussianBaseBuffer;
	public void setGausianBase(float[] gaussianBase) {
		gaussianBaseBuffer.put(gaussianBase);
		gaussianBaseBuffer.flip();
		this.gaussianBase.set(false, gaussianBaseBuffer);
	}
	
	public void setSignificance(float significance) {
		this.significance.set(significance);
	}
}
