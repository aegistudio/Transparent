package net.aegistudio.transparent.light;

import java.nio.FloatBuffer;
import java.util.Stack;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import net.aegistudio.transparent.DisplayList;
import net.aegistudio.transparent.FeatureUnsupportedException;

public class BuiltinLighting implements Lighting {
	protected final int targetLighting;
	
	protected FloatBuffer ambient, diffuse, specular;
	protected FloatBuffer lightPosition, spotDirection;
	protected float cutoff = 180.0f, exponent = 1.0f;
	protected float constantAttenuation = 1.0f, linearAttenuation = 0.0f, quadraticAttenuation = 0.0f;
	
	protected final DisplayList lightingList = new DisplayList() {
		@Override
		protected void call() {
			int lightTarget = GL11.GL_LIGHT0 + targetLighting;
			if(lightPosition != null) GL11.glLight(lightTarget, GL11.GL_POSITION, lightPosition);
			if(ambient != null) GL11.glLight(lightTarget, GL11.GL_AMBIENT, ambient);
			if(diffuse != null) GL11.glLight(lightTarget, GL11.GL_DIFFUSE, diffuse);
			if(specular != null) GL11.glLight(lightTarget, GL11.GL_SPECULAR, specular);

			if(spotDirection != null) GL11.glLight(lightTarget, GL11.GL_SPOT_DIRECTION, spotDirection);
			GL11.glLightf(lightTarget, GL11.GL_SPOT_CUTOFF, cutoff);
			GL11.glLightf(lightTarget, GL11.GL_SPOT_EXPONENT, exponent);
			GL11.glLightf(lightTarget, GL11.GL_CONSTANT_ATTENUATION, constantAttenuation);
			GL11.glLightf(lightTarget, GL11.GL_LINEAR_ATTENUATION, constantAttenuation);
			GL11.glLightf(lightTarget, GL11.GL_QUADRATIC_ATTENUATION, constantAttenuation);
		}
	};
	
	public BuiltinLighting(int targetLighting) {
		if(targetLighting < 0) 
			throw new IllegalArgumentException("Unsupported negative lighting target.");
		this.targetLighting = targetLighting;
	}
	
	protected static int maxLightings = -1;
	protected static Stack<BuiltinLighting>[] lightingStack;
	
	@SuppressWarnings("unchecked")
	protected static void initLighting() {
		if(maxLightings == -1) {
			maxLightings = GL11.glGetInteger(GL11.GL_MAX_LIGHTS);
			lightingStack = new Stack[maxLightings];
			for(int i = 0; i < maxLightings; i ++)
				lightingStack[i] = new Stack<BuiltinLighting>();
		}
	}
	
	@Override
	public void create() throws Exception {
		initLighting();
		if(this.targetLighting >= maxLightings)
			throw new FeatureUnsupportedException(
					String.format("lighting unit #%d", this.targetLighting));
		lightingList.create();
	}

	@Override
	public void use() throws Exception {
		initLighting();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_LIGHT0 + this.targetLighting);
		lightingList.use();
		lightingStack[targetLighting].push(this);
	}

	@Override
	public void recover() throws Exception {
		if(lightingStack[targetLighting].isEmpty()) {
			GL11.glDisable(GL11.GL_LIGHT0 + targetLighting);
			boolean allEmpty = true;
			for(int i = 0; i < maxLightings; i ++)
				if(!lightingStack[targetLighting].isEmpty()){
					allEmpty = false;
					break;
				}
			if(allEmpty) GL11.glDisable(GL11.GL_LIGHTING);
		}
		else {
			BuiltinLighting light = lightingStack[targetLighting].pop();
			light.lightingList.use();
		}
	}

	@Override
	public void destroy() throws Exception {
		lightingList.destroy();
	}

	protected FloatBuffer color(float r, float g, float b, float a) {
		FloatBuffer fb = BufferUtils.createFloatBuffer(4);
		fb.put(r); fb.put(g); fb.put(b); fb.put(a);	fb.flip();
		return fb;
	}
	
	public void ambient(float r, float g, float b, float a) {
		this.ambient = this.color(r, g, b, a);
		lightingList.markDirty();
	}
	
	public void diffuse(float r, float g, float b, float a) {
		this.diffuse = this.color(r, g, b, a);
		lightingList.markDirty();
	}
	
	public void specular(float r, float g, float b, float a) {
		this.specular = this.color(r, g, b, a);
		lightingList.markDirty();
	}
	
	public void spotlightDirection(float x, float y, float z, float w) {
		this.spotDirection = this.color(x, y, z, w);
		lightingList.markDirty();
	}
	
	public void lightPosition(float x, float y, float z, float w) {
		this.lightPosition = this.color(x, y, z, w);
		lightingList.markDirty();
	}
	
	public void spotlight(float cutoff, float exponent) {
		this.cutoff = cutoff;
		this.exponent = exponent;
		lightingList.markDirty();
	}
	
	public void attenuation(float k0, float k1, float k2) {
		this.constantAttenuation = k0;
		this.linearAttenuation = k1;
		this.quadraticAttenuation = k2;
		lightingList.markDirty();
	}
}
