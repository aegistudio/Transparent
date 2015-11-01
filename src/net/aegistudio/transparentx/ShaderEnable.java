package net.aegistudio.transparentx;

import net.aegistudio.transparent.shader.EnumShaderData;
import net.aegistudio.transparent.shader.Uniform;

public class ShaderEnable extends Uniform{

	public ShaderEnable(ShaderEffect effect) {
		super(EnumShaderData.BOOLEAN, String.format("enabled_%s",
				effect.getClass().getName().replaceAll("[\\.$]", "_")));
	}

	protected void getTargetStack() {
		// Do not make target stack, so no recover is needed.
	}
	
	public void enable() {
		this.set(true);
		use();
	}
	
	public void disable() {
		this.set(false);
		use();
	}
}
