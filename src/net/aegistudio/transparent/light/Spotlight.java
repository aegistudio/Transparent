package net.aegistudio.transparent.light;

import net.aegistudio.transparent.model.Effect;

public class Spotlight implements Effect{
	protected final Lighting lighting;
	
	public Spotlight(Lighting lighting) {
		this.lighting = lighting;
	}
	
	public Spotlight(int targetLighting) {
		this(new BuiltinLighting(targetLighting));
	}
	
	@Override
	public void create() throws Exception {
		lighting.create();
	}

	@Override
	public void use() throws Exception {
		lighting.use();
	}

	@Override
	public void recover() throws Exception {
		lighting.recover();
	}

	@Override
	public void destroy() throws Exception {
		lighting.destroy();
	}

	public void spotlight(float posx, float posy, float posz,
			float dirx, float diry, float dirz) {
		lighting.lightPosition(posx, posy, posz, 1.0f);
		lighting.spotlightDirection(dirx, diry, dirz, 1.0f);
	}
	
	public void spotlightParameter(float cutoff, float exponent) {
		lighting.spotlight(cutoff, exponent);
	}
	
	public void ambient(float r, float g, float b, float a) {
		lighting.ambient(r, g, b, a);
	}
	
	public void diffuse(float r, float g, float b, float a) {
		lighting.diffuse(r, g, b, a);
	}
	
	public void specular(float r, float g, float b, float a) {
		lighting.specular(r, g, b, a);
	}
}
