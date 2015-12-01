package net.aegistudio.transparent.shader;

import java.lang.reflect.Method;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;

public interface ShaderDataAction {
	public void uniform(Object... args);
	
	public void vertexAttribute(Object... args);
	
	public int getParameterLength();
	
	public class ControversalDataAction implements ShaderDataAction {
		private final int parameterLength;
		
		public ControversalDataAction(int size, String attribSuffix, Class<?> vertClazz, String uniformSuffix, Class<?> uniformClazz) throws Error {
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
		
		public ControversalDataAction(int size, String suffix, Class<?> clazz) throws Error {
			this(size, suffix, clazz, suffix, clazz);
		}
		
		private final Method vertexAttributeMethod;
		private final Method uniformMethod;
		
		public void vertexAttribute(Object... objects) {
			try {
				this.vertexAttributeMethod.invoke(null, objects);
			} catch (Exception e) {
				
			}
		}
		
		public void uniform(Object... objects) {
			try {
				this.uniformMethod.invoke(null, objects);
			}
			catch(Exception e) {
				
			}
		}
		
		public int getParameterLength() {
			return this.parameterLength;
		}
	}
	
	public class MatrixDataAction implements ShaderDataAction {
		private final Method uniformMethod;
		
		public MatrixDataAction(int matrixLength) throws Error{
			try {
				this.uniformMethod = ARBShaderObjects.class.getMethod(String.format("glUniformMatrix%dARB", matrixLength),
						int.class, boolean.class, FloatBuffer.class);
			}
			catch(Exception e) {
				throw new Error();
			}
		}
		
		/** arguments:
		 * 	args[0] : int - location
		 *  args[1] : boolean - transposed
		 *  args[2] : FloatBuffer matrix
		 **/
		
		@Override
		public void uniform(Object... args) {
			try {
				this.uniformMethod.invoke(null, args);
			}
			catch(Exception e) {
				
			}
		}

		@Override
		public void vertexAttribute(Object... args) {
			throw new IllegalArgumentException("Matrix cannot be assigned for every vertices");
		}

		@Override
		public int getParameterLength() {
			return 2;
		}
	}
}
