package net.aegistudio.transparent.light;

import net.aegistudio.transparent.model.Effect;

public interface Lighting extends Effect {
	public void ambient(float r, float g, float b, float a);
	
	public void diffuse(float r, float g, float b, float a);
	
	public void specular(float r, float g, float b, float a);
	
	public void spotlightDirection(float x, float y, float z, float w);
	
	public void lightPosition(float x, float y, float z, float w);
	
	public void spotlight(float cutoff, float exponent);
	
	public void attenuation(float k0, float k1, float k2);
}
