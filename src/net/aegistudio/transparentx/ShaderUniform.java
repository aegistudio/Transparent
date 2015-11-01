package net.aegistudio.transparentx;

import net.aegistudio.transparent.shader.EnumShaderData;
import net.aegistudio.transparent.shader.Uniform;

public class ShaderUniform extends Uniform {
	public ShaderUniform(ShaderEffect effect,
			EnumShaderData uniformData, String uniformName) {
		super(uniformData, uniformName.charAt(0) == '_'? uniformName : String.format("%s_%s", 
				effect.getClass().getName().replaceAll("[\\.$]", "_"), uniformName));
	}
	
	protected void getTargetStack() {
		// Do not make target stack.
	}
}
