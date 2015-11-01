package net.aegistudio.transparentx;

import net.aegistudio.transparentx.combine.Combine;

/**
 * Shader effect class describe a set of shader
 * effect which do almost the same things. (Shadow,
 * lighting, anti-aliasing, etc). These process are
 * assumed to have the same processing order and
 * combining method.
 * @author aegistudio
 */

public interface ShaderEffectClass {
	/**
	 * The order of the shader in the whole rendering
	 * strip. Please notice the effect of the same class
	 * should belong to the same priority.
	 * @return priority
	 */
	public double getPriority();
	
	/**
	 * How does shader effects handle a mutated variable.
	 * @return combination method
	 */
	public Combine getCombine(String mutatedVariable);
}
