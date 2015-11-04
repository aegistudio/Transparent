package net.aegistudio.transparentx.map;

import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.combine.Combine;
import net.aegistudio.transparentx.combine.NomodifyRedundant;

public class TextureMapping implements ShaderEffectClass {
	private final int order;
	private final Combine combining;
	private final String targetVariable;
	
	public TextureMapping(String targetVariable, int order, Combine combining) {
		this.order = order;
		this.combining = combining;
		this.targetVariable = targetVariable;
	}
	
	@Override
	public double getPriority() {
		return 1.0 + order;
	}

	@Override
	public Combine getCombine(String mutatedVariable) {
		if(targetVariable.equals(mutatedVariable))
			return combining;
		return new NomodifyRedundant(1);
	}
}
