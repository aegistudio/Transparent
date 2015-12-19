package net.aegistudio.transparentx.glow;

import org.lwjgl.opengl.GL11;

import net.aegistudio.transparent.fbo.EnumBufferAttachment;
import net.aegistudio.transparent.fbo.FrameBufferObject;
import net.aegistudio.transparent.fbo.RenderBuffer;
import net.aegistudio.transparent.fbo.RenderTexture;
import net.aegistudio.transparent.hint.Activator;
import net.aegistudio.transparent.hint.EnumActivable;
import net.aegistudio.transparent.image.EnumPixelFormat;
import net.aegistudio.transparent.shader.EnumShaderData;
import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparent.texture.ConstantBinding;
import net.aegistudio.transparent.texture.EnumTexture;
import net.aegistudio.transparent.texture.StackedBinding;
import net.aegistudio.transparentx.ComplexEffect;
import net.aegistudio.transparentx.ComplexRender;
import net.aegistudio.transparentx.ShaderEffect;
import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.ShaderResource;
import net.aegistudio.transparentx.ShaderUniform;

public class ScreenSizeGlowing implements ShaderEffect, ComplexEffect {

	public ScreenSizeGlowing(int width, int height) {
		fbo = new FrameBufferObject(0, 0, width, height);
		
		glowingMap = new RenderTexture(EnumTexture.SURFACE, width, height, EnumPixelFormat.BYTE4_RGBA);
		fbo.attach(EnumBufferAttachment.COLOR, glowingMap);
		
		depthBuffer = new RenderBuffer(EnumPixelFormat.BYTE_DEPTH, width, height);
		fbo.attach(EnumBufferAttachment.DEPTH, depthBuffer);
		
		stubBinding = new ConstantBinding(glowingMap, 0);
		glowingMapBinding = new StackedBinding(glowingMap, false);
	}
	
	@Override
	public ShaderEffectClass getShaderEffectClass() {
		return new GlowEffect();
	}

	ShaderResource scr_glow_fsh = new ShaderResource("scr_glow.fsh"){};
	ShaderResource scr_glow_vsh = new ShaderResource("scr_glow.vsh"){};
	
	@Override
	public String[] getRenderSource(EnumShaderType shaderType) {
		if(shaderType == EnumShaderType.VERTEX) return new String[] {scr_glow_vsh.getResource()};
		else if(shaderType == EnumShaderType.FRAGMENT) return new String[] {scr_glow_fsh.getResource()};
		else return null;
	}
	
	ShaderUniform glowingMapUniform = new ShaderUniform(this, EnumShaderData.TEXTURE, "glowMapping");
	
	@Override
	public void setParameters() {
		glowingMapUniform.set(glowingMapBinding);
		glowingMapUniform.use();
	}

	@Override
	public void create() {
		fbo.create();
		glowingMap.create();
		depthBuffer.create();
		
		stubBinding.create();
		glowingMapBinding.create();
	}
	FrameBufferObject fbo;
	RenderTexture glowingMap;
	RenderBuffer depthBuffer;
	ConstantBinding stubBinding;
	StackedBinding glowingMapBinding;
	
	@Override
	public void prerender(ComplexRender controller) throws Exception {
		fbo.push();
		GL11.glClearColor(0, 0, 0, 0);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		controller.self(GlowingEffectProgram.class);
		fbo.pop();
		
		glowingMapBinding.use();
	}

	Activator depthTest = new Activator(EnumActivable.DEPTH_TEST, false);
	Activator lighting = new Activator(EnumActivable.LIGHTING, false);
	
	@Override
	public void postrender(ComplexRender controller) throws Exception {
		glowingMapBinding.recover();
	}

	@Override
	public void destroy() {
		glowingMap.destroy();
		depthBuffer.destroy();
		fbo.destroy();
		stubBinding.destroy();
		glowingMapBinding.destroy();
	}
}
