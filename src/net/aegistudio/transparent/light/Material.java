package net.aegistudio.transparent.light;

import java.nio.FloatBuffer;
import java.util.Stack;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import net.aegistudio.transparent.DisplayList;
import net.aegistudio.transparent.model.Effect;

public class Material implements Effect {
	protected FloatBuffer ambient, diffuse, specular, emission;
	protected float shininess = 0;
	
	protected int face = GL11.GL_FRONT_AND_BACK;
	
	protected final DisplayList material = new DisplayList() {
		@Override
		protected void call() {
			if(ambient != null) GL11.glMaterial(face, GL11.GL_AMBIENT, ambient);
			if(diffuse != null) GL11.glMaterial(face, GL11.GL_DIFFUSE, diffuse);
			if(specular != null) GL11.glMaterial(face, GL11.GL_SPECULAR, specular);
			if(emission != null) GL11.glMaterial(face, GL11.GL_EMISSION, emission);
			GL11.glMaterialf(face, GL11.GL_SHININESS, shininess);
		}
	};
	
	public Material() {
		this.ambient(0.2f, 0.2f, 0.2f, 1.0f);
		this.diffuse(0.8f, 0.8f, 0.8f, 1.0f);
		this.specular(0f, 0f, 0f, 1.0f);
		this.emission(0f, 0f, 0f, 1.0f);
		this.shininess(0f);
	}
	
	@Override
	public void create() throws Exception {
		material.create();
	}

	@Override
	public void use() throws Exception {
		material.use();
		materials.push(this);
	}

	@Override
	public void recover() throws Exception {
		if(!materials.isEmpty()) {
			Material material = materials.pop();
			material.material.use();
		}
	}

	@Override
	public void destroy() throws Exception {
		material.destroy();
	}

	protected static Stack<Material> materials = new Stack<Material>();
	
	protected FloatBuffer color(float r, float g, float b, float a) {
		FloatBuffer fb = BufferUtils.createFloatBuffer(4);
		fb.put(r); fb.put(g); fb.put(b); fb.put(a);	fb.flip();
		return fb;
	}
	
	public void ambient(float r, float g, float b, float a) {
		this.ambient = this.color(r, g, b, a);
		material.markDirty();
	}
	
	public void diffuse(float r, float g, float b, float a) {
		this.diffuse = this.color(r, g, b, a);
		material.markDirty();
	}
	
	public void specular(float r, float g, float b, float a) {
		this.specular = this.color(r, g, b, a);
		material.markDirty();
	}
	
	public void emission(float r, float g, float b, float a) {
		this.emission = this.color(r, g, b, a);
		material.markDirty();
	}
	
	public void shininess(float shininess) {
		this.shininess = shininess;
		material.markDirty();
	}
}
