package net.aegistudio.transparentx.combine;

import net.aegistudio.transparent.hint.EnumBlendMethod;

/**
 * This would only happen in vectors, the w-component
 * or alpha-component will help 
 * 
 * @author aegistudio
 */

public class ModifyBlendOriginal implements Combine{
	protected final EnumBlendMethod blendMethod;
	private String preprocessedCode;
	private String postProcessedCode;
	
	public ModifyBlendOriginal(EnumBlendMethod blendMethod) {
		this.blendMethod = blendMethod;
	}
	
	@Override
	public void combine(String type, String target, String prefix) {
		this.preprocessedCode = String.format("%s %s_%s = %s;", type, prefix, target, target);
		this.postProcessedCode = this.blendMethod.getFormula().replaceAll("%result", target)
				.replaceAll("%source", String.format("%s_%s", prefix, target)).replaceAll("%destination", target).concat(";");
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
