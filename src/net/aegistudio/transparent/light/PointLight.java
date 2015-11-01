package net.aegistudio.transparent.light;

import net.aegistudio.transparent.model.Effect;

public class PointLight implements Effect{
	
	protected final Lighting lighting;
	
	public PointLight(Lighting lighting) {
		this.lighting = lighting;
	}
	
	public PointLight(int targetLighting) {
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

	public void position(float x, float y, float z) {
		lighting.lightPosition(x, y, z, 1.0f);
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
