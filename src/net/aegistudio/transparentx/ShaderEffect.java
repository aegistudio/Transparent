package net.aegistudio.transparentx;

import net.aegistudio.transparent.model.Drawable;
import net.aegistudio.transparent.shader.EnumShaderType;

/**
 * A shader effect of one class is the same at code
 * but might be some different in data.
 * 
 * @author aegistudio
 */

public interface ShaderEffect {
	public ShaderEffectClass getShaderEffectClass();
	
	public boolean shouldPrerender();
	
	public void doPrerender(Drawable prerendering);

	/**
	 * <b>Caution:</b> the main function should be in the first
	 * shader source. <br>
	 * 
	 * <b>Caution:</b> variables begin with '_' is shared
	 * variable, which means different shaders can access
	 * them. please don't use '_'-prefixed variables if
	 * you don't want to share them.
	 * 
	 * @param shaderType the required shader type.
	 * @return the source of that shader. Every return string
	 * will be designated to an object.
	 */
	public String[] getRenderSource(EnumShaderType shaderType);
	
	public void setParameters();
}
