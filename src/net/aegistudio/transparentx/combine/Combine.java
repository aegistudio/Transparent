package net.aegistudio.transparentx.combine;

public interface Combine {
	
	/**
	 * Pass the variables and prefix, to get the combination
	 * result.
	 * 
	 * @param target type of the variable.
	 * @param target the target of combination.
	 * @param prefix the prefix of the shader.
	 */
	
	public void combine(String type, String target, String prefix);
	
	/**
	 * @return the code before calling the shader.
	 */
	
	public String getPreprocessCode();
	
	/**
	 * @return the code after calling the shader.
	 */
	
	public String getPostprocessCode();
	
	/**
	 * @see net.aegistudio.transparentx.lang.ShaderPreprocessor
	 * @return the replacement count when preprocessing the shader.
	 */
	public int getReplacement();
}
