package net.aegistudio.transparentx;

import net.aegistudio.transparent.shader.ShaderException;

public interface ShaderEffectProgram {
	public boolean adapt(ShaderEffect effect) throws Exception;
	
	public void recompile() throws ShaderException;
	
	public void create();
	
	public void push();
	
	public void pop();
}
