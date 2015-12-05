package net.aegistudio.transparent.mvp;

import java.nio.FloatBuffer;

public class Matrix {
	public static void get(float[] result, FloatBuffer floatBuffer) {
		int i = 0;
		while(floatBuffer.hasRemaining()) {
			result[i] = floatBuffer.get();
			i ++;
		}
		floatBuffer.flip();
	}
	
	/** Input Should Be 4x4 Matrix (Array Of Length 16) **/
	public static void inverse(float input[], float result[]) {
		float determinant = determinant4x4(input);
		for(int i = 0; i < 4; i ++)
			for(int j = 0; j < 4; j ++)
				set(result, j, i, ((i + j) % 2 == 0? 1 : -1) * determinant3x3Remain(input, i, j) / determinant);
	}
	
	static int skipIndex1[] = {1, 0, 0, 0};
	static int skipIndex2[] = {2, 2, 1, 1};
	static int skipIndex3[] = {3, 3, 3, 2};
	
	/** Input Should Be 4x4 Matrix With SkipRow And SkipColumn in [0, 4) **/
	public static float determinant3x3Remain(float input[], int skipRow, int skipColumn) {
		float x11 = get(input, skipIndex1[skipRow], skipIndex1[skipColumn]);
		float x12 = get(input, skipIndex1[skipRow], skipIndex2[skipColumn]);
		float x13 = get(input, skipIndex1[skipRow], skipIndex3[skipColumn]);
		
		float x21 = get(input, skipIndex2[skipRow], skipIndex1[skipColumn]);
		float x22 = get(input, skipIndex2[skipRow], skipIndex2[skipColumn]);
		float x23 = get(input, skipIndex2[skipRow], skipIndex3[skipColumn]);
		
		float x31 = get(input, skipIndex3[skipRow], skipIndex1[skipColumn]);
		float x32 = get(input, skipIndex3[skipRow], skipIndex2[skipColumn]);
		float x33 = get(input, skipIndex3[skipRow], skipIndex3[skipColumn]);
		
		return x11 * (x22 * x33 - x32 * x23) + x12 * (x23 * x31 - x33 * x21) + x13 * (x21 * x32 - x31 * x22);
	}
	
	public static float determinant4x4(float input[]) {
		return input[0] * determinant3x3Remain(input, 0, 0)
			- input[1] * determinant3x3Remain(input, 0, 1)
			+ input[2] * determinant3x3Remain(input, 0, 2)
			- input[3] * determinant3x3Remain(input, 0, 3);
	}
	
	public static float get(float input[], int r, int c) {
		return input[4 * r + c];
	}
	
	public static void set(float input[], int r, int c, float value) {
		input[4 * r + c] = value;
	}
	
	public static void multiply4x4(float[] left, float right[], float[] result) {
		for(int i = 0; i < 4; i ++)
			for(int j = 0; j < 4; j ++) {
				float cell = 0;
				for(int k = 0; k < 4; k ++)
					cell += get(left, i, k) * get(right, k, j);
				set(result, i, j, cell);
			}
	}
	
	public static void copy(float[] left, float[] right) {
		for(int i = 0; i < left.length; i ++)
			right[i] = left[i];
	}
	
	public static void multiply(float[] left, float right[], float[] result) {
		for(int i = 0; i < 4; i ++) {
				float cell = 0;
				for(int k = 0; k < 4; k ++)
					cell += get(left, i, k) * right[k];
				result[i] = cell;
			}
	}
	
	/** Make a vector to be a unit vector **/
	public static void normalize(float[] vector) {
		float modulus = (float) Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
		vector[0] /= modulus; vector[1] /= modulus; vector[2] /= modulus; 
	}
	
	public static void cross(float[] left, float[] right, float[] result) {
		/**
		 * |i			j			k		|
		 * |left[0]		left[1]		left[2]	|
		 * |right[0]	right[1]	right[2]|
		 */
		
		result[0] = left[1] * right[2] - right[1] * left[2];
		result[1] = left[2] * right[0] - right[2] * left[0];
		result[2] = left[0] * right[1] - right[0] * left[1];
		result[3] = 0;
	}
	
	public static float dot(float[] left, float[] right) {
		return left[0] * right[0] + left[1] * right[1] + left[2] * right[2] + left[3] * right[3];
	}
}
