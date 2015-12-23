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
import net.aegistudio.transparent.texture.EnumTexture;
import net.aegistudio.transparent.texture.StackedBinding;
import net.aegistudio.transparentx.ComplexEffect;
import net.aegistudio.transparentx.ComplexRender;
import net.aegistudio.transparentx.ShaderEffect;
import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.ShaderResource;
import net.aegistudio.transparentx.ShaderUniform;

/**
 * A glow effect based on the glowing map rendered on the same perspective
 * as the normal render process. 
 * <br><br>
 * 
 * <b>Warning: </b>To make this effect work properly, this effect should be 
 * inside super node of nodes that contain glow sub-effects. And only nodes
 * that are under the super node could be influenced by this effect.
 * 
 * @author aegistudio
 */

public class ScreenSizeGlowing implements ShaderEffect, ComplexEffect {

	/**
	 * @param width 	The width of the glow map.
	 * @param height	The height of the glow map.
	 */
	public ScreenSizeGlowing(int width, int height) {
		fbo = new FrameBufferObject(0, 0, width, height);
		
		glowingMap = new RenderTexture(EnumTexture.SURFACE, width, height, EnumPixelFormat.BYTE4_RGBA);
		fbo.attach(EnumBufferAttachment.COLOR, glowingMap);
		
		depthBuffer = new RenderBuffer(EnumPixelFormat.BYTE_DEPTH, width, height);
		fbo.attach(EnumBufferAttachment.DEPTH, depthBuffer);
		glowingMapBinding = new StackedBinding(glowingMap, false);
		
		this.glowFormula(1.0f, 0.0f, 0.0f, 3.5f);
		this.glowQuality(0.04f, 0.4f, 5);
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

	ShaderUniform zVectorUniform = new ShaderUniform(this, EnumShaderData.VEC4, "zVector");
	
	ShaderUniform radius = new ShaderUniform(this, EnumShaderData.FLOAT, "radius");
	ShaderUniform sampleDirections = new ShaderUniform(this, EnumShaderData.FLOAT, "sampleDirections");
	ShaderUniform amplification = new ShaderUniform(this, EnumShaderData.FLOAT, "amplification");

	FrameBufferObject fbo;
	RenderTexture glowingMap;
	RenderBuffer depthBuffer;
	StackedBinding glowingMapBinding;
	
	@Override
	public void create() {
		fbo.create();
		glowingMap.create();
		depthBuffer.create();
		glowingMapBinding.create();
		
		zVectorUniform.create();
		amplification.create();
		radius.create();
		sampleDirections.create();
	}
	
	@Override
	public void destroy() {
		glowingMap.destroy();
		depthBuffer.destroy();
		fbo.destroy();
		glowingMapBinding.destroy();
		
		zVectorUniform.destroy();
		amplification.destroy();
		radius.destroy();
		sampleDirections.destroy();
	}
	
	@Override
	public void setParameters() {
		glowingMapUniform.set(glowingMapBinding);
		glowingMapUniform.use();
		
		zVectorUniform.use();
		amplification.use();
		radius.use();
		sampleDirections.use();
	}
	
	/**
	 * How will the glow attenuates as it goes farther from the glow body.
	 * The formula is 1/(c0 * r + c1 * r + c2 * r^2) ^ exp. While r is the 
	 * distance from the glow body. And the light value of a fragment pixel
	 * will be $$\int(-d, d) \int(-d, d) form(x0 + x, y0 + y)
	 *  * sample(x0 + x, y0 + y) dxdy$$, where form is the formula, and 
	 *  sample is the light value on the glow map.
	 */
	
	public void glowFormula(float c0, float c1, float c2, float exp) {
		zVectorUniform.set(c0, c1, c2, exp);
	}
	
	/**
	 * Set the quality of the glow parameters. The radius affects the range of glow, 
	 * the amplification affects the shininess of the glow, and direction affects the
	 * precision of the glow.
	 * 
	 * @param radius the shining radium for all glows. The greater value, the wider range.
	 * @param amplification the light value for glows. The greater value, the greater lightness.
	 * @param direction how many samples should the gaussian kernel do with. 
	 * 		The greater value, the preciser effect. 
	 * 		Had better greater than 3, and 5 is default value.
	 */
	
	public void glowQuality(float radius, float amplification, int direction) {
		this.radius.set(radius);
		this.amplification.set(amplification);
		this.sampleDirections.set((float)direction);
	}
	
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
}

