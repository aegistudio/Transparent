package net.aegistudio.transparent.shader;

import net.aegistudio.transparent.shader.ShaderDataAction.ControversalDataAction;
import net.aegistudio.transparent.shader.ShaderDataAction.MatrixDataAction;
import net.aegistudio.transparent.texture.TextureBinding;

public enum EnumShaderData
{
	BOOLEAN(new ControversalDataAction(1, "s", short.class, "i", int.class)) {
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
	
	FLOAT(new ControversalDataAction(1, "f", float.class)),
	VEC2(new ControversalDataAction(2, "f", float.class)),
	VEC3(new ControversalDataAction(3, "f", float.class)),
	VEC4(new ControversalDataAction(4, "f", float.class)),
	
	INT(new ControversalDataAction(1, "s", short.class, "i", int.class)),
	IVEC2(new ControversalDataAction(2, "s", short.class, "i", int.class)),
	IVEC3(new ControversalDataAction(3, "s", short.class, "i", int.class)),
	IVEC4(new ControversalDataAction(4, "s", short.class, "i", int.class)),
	
	DOUBLE(new ControversalDataAction(1, "d", double.class, "f", float.class)),
	DVEC2(new ControversalDataAction(2, "d", double.class, "f", float.class)),
	DVEC3(new ControversalDataAction(3, "d", double.class, "f", float.class)),
	DVEC4(new ControversalDataAction(4, "d", double.class, "f", float.class)),
	
	TEXTURE(new ControversalDataAction(1, "s", short.class, "i", int.class)){
		public void vertexAttribute(Object... objects) {
			throw new IllegalArgumentException("Texture cannot be assigned for every vertices");
		}
		
		public void uniform(Object... objects) {
			if(objects[1] instanceof TextureBinding)
				super.uniform(objects[0], 
						((TextureBinding)objects[1]).getCurrentBinding());
			else super.uniform(objects);
		}
	},
	MATRIX2(new MatrixDataAction(2)),
	MATRIX3(new MatrixDataAction(3)),
	MATRIX4(new MatrixDataAction(4));
	
	private final ShaderDataAction shaderDataAction;
	
	private EnumShaderData(ShaderDataAction shaderDataAction) throws Error {
			this.shaderDataAction = shaderDataAction;
	}
	
	public void vertexAttribute(Object... objects) {
		try {
			int parameterLength = this.getParameterLength();
			if(parameterLength + 1 != objects.length)
				throw new IllegalArgumentException(
						String.format("Mismatch between parameter length! Required %d, actually %d",
								parameterLength + 1, objects.length
				));
			this.shaderDataAction.vertexAttribute(objects);
		}
		catch(IllegalArgumentException iae) {
			throw iae;
		}
		catch(Exception e) {
			
		}
	}
	
	public void uniform(Object... objects) {
		try {
			int parameterLength = this.getParameterLength();
			if(parameterLength + 1 != objects.length)
				throw new IllegalArgumentException(
						String.format("Mismatch between parameter length! Required %d, actually %d",
								parameterLength + 1, objects.length
				));
			this.shaderDataAction.uniform(objects);
		}
		catch(IllegalArgumentException iae) {
			throw iae;
		}
		catch(Exception e) {
			
		}
	}
	
	public int getParameterLength() {
		return this.shaderDataAction.getParameterLength();
	}
}

