package net.aegistudio.transparentx.ao;

import org.lwjgl.opengl.GL11;

import net.aegistudio.transparent.fbo.EnumBufferAttachment;
import net.aegistudio.transparent.fbo.RenderTexture;
import net.aegistudio.transparent.image.EnumPixelFormat;
import net.aegistudio.transparent.shader.EnumShaderData;
import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparent.texture.EnumTexture;
import net.aegistudio.transparent.texture.StackedBinding;
import net.aegistudio.transparentx.ComplexEffect;
import net.aegistudio.transparentx.ComplexRender;
import net.aegistudio.transparentx.DefaultEffectProgram;
import net.aegistudio.transparentx.ShaderEffect;
import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.ShaderResource;
import net.aegistudio.transparentx.ShaderUniform;

/**
 * This is a form of SSAO (Screen-Size Ambient Occlusion) in a limited grid.
 * Affect on frame rate may be inevitable, please notice.
 * @author aegistudio
 */

public class ProximateAmbientOcclusion implements ShaderEffect, ComplexEffect{

	private int detailLevel;
	private float windowSize;
	
	private RenderTexture depthMap;
	
	/**
	 * @param samplePoints How many sample points are there in the depth frame buffer.
	 * @param detailLevel How much detail of occlusion do you need.
	 * @param windowSize How far does the occlusion sampling window go.
	 */
	
	public ProximateAmbientOcclusion(int samplePoints, int detailLevel, float windowSize) {
		this.detailLevel = detailLevel;
		this.windowSize = windowSize;
		
		this.depthMap = new RenderTexture(EnumTexture.SURFACE, samplePoints, samplePoints, 
				EnumPixelFormat.BYTE_DEPTH, EnumBufferAttachment.DEPTH);
		this.binding = new StackedBinding(depthMap, false);
	}
	
	@Override
	public void create() {
		depthMap.create();
	}

	StackedBinding binding;

	@Override
	public void prerender(ComplexRender controller) throws Exception {
		depthMap.push();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		controller.self(DefaultEffectProgram.class);
		depthMap.pop();
	
		binding.use();
	}

	@Override
	public void postrender(ComplexRender controller) throws Exception {
		binding.recover();
	}

	@Override
	public void destroy() {
		depthMap.destroy();
	}

	@Override
	public ShaderEffectClass getShaderEffectClass() {
		return new AmbientOcclusion();
	}

	ShaderResource gpao_vsh = new ShaderResource("gpao.vsh"){};
	ShaderResource gpao_fsh = new ShaderResource("gpao.fsh"){};
	
	@Override
	public String[] getRenderSource(EnumShaderType shaderType) {
		if(shaderType == EnumShaderType.VERTEX) return new String[] {gpao_vsh.getResource()};
		else if(shaderType == EnumShaderType.FRAGMENT) return new String[] {gpao_fsh.getResource()};
		return null;
	}

	ShaderUniform depthMapUniform = new ShaderUniform(this, EnumShaderData.TEXTURE, "depthMap");
	ShaderUniform detailLevelUniform = new ShaderUniform(this, EnumShaderData.INT, "detailLevel");
	ShaderUniform windowSizeUniform = new ShaderUniform(this, EnumShaderData.FLOAT, "windowSize");
	
	@Override
	public void setParameters() {
		depthMapUniform.set(binding);
		depthMapUniform.use();
		detailLevelUniform.set(detailLevel);
		detailLevelUniform.use();
		windowSizeUniform.set(windowSize);
		windowSizeUniform.use();
	}	
}
