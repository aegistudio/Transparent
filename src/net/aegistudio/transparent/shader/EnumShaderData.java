package net.aegistudio.transparent.shader;

import java.lang.reflect.Method;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import net.aegistudio.transparent.texture.TextureBinding;

public enum EnumShaderData
{
	BOOLEAN(1, "s", short.class, "i", int.class) {
		public void vertexAttribute(Object... objects) {	
			Class<?> clazz = objects[1].getClass();
			if(clazz == boolean.class || clazz == Boolean.class)
				super.vertexAttribute(objects[0],
						(boolean) objects[1]? 1 : 0);
			else throw new IllegalArgumentException("Boolean value excepted!");
		}
		
		public void uniform(Object... objects) {
			Class<?> clazz = objects[1].getClass();
			if(clazz == boolean.class || clazz == Boolean.class)
				super.uniform(objects[0],
						(boolean) objects[1]? 1 : 0);
			else throw new IllegalArgumentException("Boolean value excepted!");
		}
	},
	/**
	BVEC2(2, "s", short.class, "i", int.class),
	BVEC3(2, "s", short.class, "i", int.class),
	BVEC4(2, "s", short.class, "i", int.class),
	**/
	
	FLOAT(1, "f", float.class),
	VEC2(2, "f", float.class),
	VEC3(3, "f", float.class),
	VEC4(4, "f", float.class),
	
	INT(1, "s", short.class, "i", int.class),
	IVEC2(2, "s", short.class, "i", int.class),
	IVEC3(3, "s", short.class, "i", int.class),
	IVEC4(4, "s", short.class, "i", int.class),
	
	DOUBLE(1, "d", double.class, "f", float.class),
	DVEC2(2, "d", double.class, "f", float.class),
	DVEC3(3, "d", double.class, "f", float.class),
	DVEC4(4, "d", double.class, "f", float.class),
	
	TEXTURE(1, "s", short.class, "i", int.class){
		public void vertexAttribute(Object... objects) {
			throw new IllegalArgumentException("Texture cannot be assigned for every vertices");
		}
		
		public void uniform(Object... objects) {
			if(objects[1] instanceof TextureBinding)
				super.uniform(objects[0], 
						((TextureBinding)objects[1]).getCurrentBinding());
			else super.uniform(objects);
		}
	};
	
	private int parameterLength;
	
	private EnumShaderData(int size, String attribSuffix, Class<?> vertClazz, String uniformSuffix, Class<?> uniformClazz) throws Error {
		try {
			parameterLength = size;
			Class<?>[] vertexClasses = new Class<?>[size + 1];
			Class<?>[] uniformClasses = new Class<?>[size + 1];
			
			for(int i = 0; i < size; i ++) {
				vertexClasses[i + 1] = vertClazz;
				uniformClasses[i + 1] = uniformClazz;
			}
			
			vertexClasses[0] = int.class;
			uniformClasses[0] = int.class;
			
			this.vertexAttributeMethod = ARBVertexShader.class.getMethod(String.format("glVertexAttrib%d%sARB", size, attribSuffix), vertexClasses);
			this.uniformMethod = ARBShaderObjects.class.getMethod(String.format("glUniform%d%sARB", size, uniformSuffix), uniformClasses);
		}
		catch(Exception e) {
			throw new Error();
		}
	}
	
	private EnumShaderData(int size, String suffix, Class<?> clazz) throws Error {
		this(size, suffix, clazz, suffix, clazz);
	}
	
	private final Method vertexAttributeMethod;
	private final Method uniformMethod;
	
	
	public void vertexAttribute(Object... objects) {
		try {
			if(parameterLength + 1 != objects.length)
				throw new IllegalArgumentException(
						String.format("Mismatch between parameter length! Required %d, actually %d",
								parameterLength + 1, objects.length
				));
			this.vertexAttributeMethod.invoke(null, objects);
		}
		catch(IllegalArgumentException iae) {
			throw iae;
		}
		catch(Exception e) {
			
		}
	}
	
	public void uniform(Object... objects) {
		try {
			if(parameterLength + 1 != objects.length)
				throw new IllegalArgumentException(
						String.format("Mismatch between parameter length! Required %d, actually %d",
								parameterLength + 1, objects.length
				));
			this.uniformMethod.invoke(null, objects);
		}
		catch(IllegalArgumentException iae) {
			throw iae;
		}
		catch(Exception e) {
			
		}
	}
	
	public int getParameterLength() {
		return this.parameterLength;
	}
}

