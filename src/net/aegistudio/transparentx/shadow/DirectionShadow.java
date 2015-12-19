package net.aegistudio.transparentx.shadow;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import net.aegistudio.transparent.fbo.EnumBufferAttachment;
import net.aegistudio.transparent.fbo.FrameBufferObject;
import net.aegistudio.transparent.fbo.RenderTexture;
import net.aegistudio.transparent.image.EnumPixelFormat;
import net.aegistudio.transparent.mvp.Matrix;
import net.aegistudio.transparent.shader.EnumShaderData;
import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparent.texture.StackedBinding;
import net.aegistudio.transparent.texture.TextureBinding;
import net.aegistudio.transparentx.ComplexEffect;
import net.aegistudio.transparentx.ComplexRender;
import net.aegistudio.transparentx.ShaderEffect;
import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.ShaderResource;
import net.aegistudio.transparentx.ShaderUniform;

public abstract class DirectionShadow implements ShaderEffect, ComplexEffect {
	protected final FrameBufferObject fbo;
	protected final RenderTexture renderTexture;
	protected final TextureBinding binding;
	protected final int lightTarget;
	
	public DirectionShadow(int targetLightSource, int shadowDetailLevel) {
		shadowClass = new Shadow(Shadow.DIRECTIONAL, targetLightSource);
		lightTarget = targetLightSource;
		fbo = new FrameBufferObject(0, 0, shadowDetailLevel, shadowDetailLevel);
		renderTexture = new RenderTexture(shadowDetailLevel, shadowDetailLevel, 
					EnumPixelFormat.BYTE_DEPTH);
		fbo.attach(EnumBufferAttachment.DEPTH, renderTexture);
		
		this.binding = new StackedBinding(renderTexture, false);
		shadowMap.set(binding);
		
		FloatBuffer coordTransformBuffer = BufferUtils
				.createFloatBuffer(16).put(new float[]{
				0.5f,	0,		0, 		-0.5f,
				0,		0.5f,	0, 		-0.5f,
				0,		0,    	0.5f,  	0,
				0,		0,    	0,     	1
		});
		coordTransformBuffer.flip();
		coordTransform.set(true, coordTransformBuffer);
		regulation.set(0.1f);
	}
	
	public DirectionShadow(int targetLightSource) {
		this(targetLightSource, 1 << 10);
	}
	
	protected final Shadow shadowClass;
	@Override
	public ShaderEffectClass getShaderEffectClass() {
		return shadowClass;
	}
	
	ShaderResource directional_shadow_vsh = new ShaderResource("direction_shadow.vsh"){};
	ShaderResource directional_shadow_fsh = new ShaderResource("direction_shadow.fsh"){};
	
	@Override
	public String[] getRenderSource(EnumShaderType shaderType) {
		if(shaderType == EnumShaderType.VERTEX) return new String[]{directional_shadow_vsh.getResource()};
		else if(shaderType == EnumShaderType.FRAGMENT) return new String[]{directional_shadow_fsh.getResource()
				.replaceAll("%targetLight", Integer.toString(lightTarget))};
		else return null;
	}

	ShaderUniform shadowMap = new ShaderUniform(this, EnumShaderData.TEXTURE, "shadowMap");
	
	// lightTransform = PL * Wb * inverse(MVP)
	ShaderUniform lightTransform = new ShaderUniform(this, EnumShaderData.MATRIX4, "lightTransform");
	ShaderUniform coordTransform = new ShaderUniform(this, EnumShaderData.MATRIX4, "coordTransform");
	ShaderUniform projTransform = new ShaderUniform(this, EnumShaderData.MATRIX4, "projTransform");
	ShaderUniform directionalLight = new ShaderUniform(this, EnumShaderData.BOOLEAN, "directionalLight");
	
	@Override
	public void setParameters() {
		shadowMap.use();
		coordTransform.use();
		lightTransform.use();
		projTransform.use();
		directionalLight.use();
		regulation.use();
	}

	@Override
	public void create() {
		renderTexture.create();
		fbo.create();
	}
	
	ShaderUniform regulation = new ShaderUniform(this, EnumShaderData.FLOAT, "regulation");
	public void setRegulation(float regulation) {
		this.regulation.set(regulation);
	}
	
	FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	
	float[] projection = new float[16];
	FloatBuffer projectionBuffer = BufferUtils.createFloatBuffer(16);
	
	float[] modelview = new float[16];
	float[] result = new float[16];
	float[] result2 = new float[16];
	
	FloatBuffer vectorBuffer = BufferUtils.createFloatBuffer(4);
	float[] lightPos = new float[4];
	float[] result3 = new float[4];
	
	float[] z_m = new float[4];
	
	float[] wbase = new float[16];
	FloatBuffer wbaseBuffer = BufferUtils.createFloatBuffer(16);
	
	float[][] eigenVertices = new float[][] {
		{1.0f, -1.0f, 1.0f, 1.0f}, 
		{1.0f, 1.0f, 1.0f, 1.0f}, 
		{-1.0f, -1.0f, 1.0f, 1.0f},
		{-1.0f, 1.0f, 1.0f, 1.0f},
		{1.0f, -1.0f, -1.0f, 1.0f},
		{1.0f, 1.0f, -1.0f, 1.0f}, 
		{-1.0f, -1.0f, -1.0f, 1.0f},
		{-1.0f, 1.0f, -1.0f, 1.0f}
	};
	
	FloatBuffer lightTransformBuffer = BufferUtils.createFloatBuffer(16);
	
	boolean directionalLightFlag = false;
	@Override
	public void prerender(ComplexRender controller) throws Exception {
		GL11.glGetLight(GL11.GL_LIGHT0 + lightTarget, GL11.GL_POSITION, vectorBuffer);
		Matrix.get(lightPos, vectorBuffer);
		
		directionalLightFlag = (lightPos[3] == 0); // lightPos.W == 0?
		directionalLight.set(directionalLightFlag);
		
		if(directionalLightFlag) {
			GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, matrixBuffer);
			Matrix.get(modelview, matrixBuffer);
			GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, matrixBuffer);
			Matrix.get(projection, matrixBuffer);
			
			float[] mvp = result; float[] inverseMvp = result2;
			Matrix.multiply4x4(projection, modelview, mvp);			// Result = MVP
			Matrix.inverse(mvp, inverseMvp);						// Result2 = inverse(MVP)
	
			{
			//	float[] regularizedLightPos = result3;
			//	Matrix.multiply(mvp, lightPos, regularizedLightPos);			
			//	Matrix.copy(regularizedLightPos, lightPos);
				Matrix.normalize(lightPos);								// LightPos = LP (World Coordinate)
				
				for(int i = 0; i < 4; i ++)
					z_m[i] = Matrix.get(inverseMvp, i, 2);					// Z_M = inverse(MVP) * (0, 0, 1, 0)
				Matrix.normalize(z_m);
			}
			
			//XXX There's something wrong with the calculation, and should be notice.
			float[] inverseProj = result;
			Matrix.inverse(projection, inverseProj);
			projectionBuffer.put(inverseProj);
			projTransform.set(true, projectionBuffer);
			projectionBuffer.flip();
			
			// WBase Calculation
			float[] orthoVec = result3;
			Matrix.cross(lightPos, z_m, orthoVec);
			if(Matrix.dot(orthoVec, orthoVec) == 0) {
				// Z.direction == lightDirection or -lightDirection
				int symbol = Matrix.dot(orthoVec, z_m) >= 0? 1 : -1;
				for(int i = 0; i < 4; i ++)
					for(int j = 0; j < 4; j ++)
						Matrix.set(wbase, i, j, i == j? symbol : 0);
			}
			else {
				// Z_M.direction != lightDirection
				Matrix.normalize(orthoVec);
				float[] z_n = z_m;
				Matrix.cross(orthoVec, lightPos, z_n);
				Matrix.normalize(z_n);
			
				for(int i = 0; i < 4; i ++) {
					Matrix.set(wbase, i, 1, orthoVec[i]);
					Matrix.set(wbase, i, 0, z_n[i]);
					Matrix.set(wbase, i, 2, lightPos[i]);
					Matrix.set(wbase, i, 3, 0);
				}
				wbase[15] = 1;
			}
			
			wbaseBuffer.put(wbase);
			wbaseBuffer.flip();
			
			Matrix.multiply4x4(wbase, inverseMvp, result);	// Result = WBase * inverse(MVP)
			
			float xmax = - Float.MAX_VALUE;	float xmin = Float.MAX_VALUE;
			float ymax = - Float.MAX_VALUE;	float ymin = Float.MAX_VALUE;
			float zmax = - Float.MAX_VALUE;	float zmin = Float.MAX_VALUE;
			
			float[] lightSpaceVertices = result3;
			for(int i = 0; i < eigenVertices.length; i ++) {
				float[] current = eigenVertices[i];
				Matrix.multiply(result, current, lightSpaceVertices);
				if(lightSpaceVertices[0] > xmax) xmax = lightSpaceVertices[0];
				if(lightSpaceVertices[0] < xmin) xmin = lightSpaceVertices[0];
				if(lightSpaceVertices[1] > ymax) ymax = lightSpaceVertices[1];
				if(lightSpaceVertices[1] < ymin) ymin = lightSpaceVertices[1];
				if(lightSpaceVertices[2] > zmax) zmax = lightSpaceVertices[2];
				if(lightSpaceVertices[2] < zmin) zmin = lightSpaceVertices[2];
			}
			
			fbo.push();
			/* subrendering(shadowTexture) */ {
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
				
				GL11.glMatrixMode(GL11.GL_PROJECTION);
				GL11.glPushMatrix();
				GL11.glLoadIdentity();
				GL11.glOrtho(xmin, xmax, ymin, ymax, zmax, zmin);
				GL11.glMultMatrix(wbaseBuffer);
				GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, lightTransformBuffer);
				lightTransform.set(false, lightTransformBuffer);
				
				controller.self(null);
				
				GL11.glMatrixMode(GL11.GL_PROJECTION);
				GL11.glPopMatrix();
			}
			fbo.pop();
	
			binding.use();
		}
	}

	@Override
	public void postrender(ComplexRender controller) throws Exception {
		if(directionalLightFlag)
			binding.recover();
	}

	@Override
	public void destroy() {
		fbo.destroy();
		renderTexture.destroy();
	}
}
