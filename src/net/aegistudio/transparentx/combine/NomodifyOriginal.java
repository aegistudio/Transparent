package net.aegistudio.transparentx.combine;

/**
 * No-modify original means the result won't be changed
 * after calling the shader. But the variable itself should
 * be recalculated in the shader.
 * 
 * which means<br>
 * 
 * variableType prefix_gl_Variable = gl_Variable;
 * prefix_main();
 * gl_Variable = prefix_gl_Variable;
 * 
 * @author aegistudio
 */

public class NomodifyOriginal implements Combine{
	private String preprocessedCode;
	private String postProcessedCode;
	
	@Override
	public void combine(String type, String target, String prefix) {
		this.preprocessedCode = String.format("%s %s_%s = %s;", type, prefix, target, target);
		this.postProcessedCode = String.format("%s = %s_%s;", target, prefix, target);
	}

	@Override
	public String getPreprocessCode() {
		return preprocessedCode;
	}

	@Override
	public String getPostprocessCode() {
		return postProcessedCode;
	}

	@Override
	public int getReplacement() {
		return 0;
	}
}
