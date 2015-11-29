package net.aegistudio.transparentx.map;

import java.util.TreeSet;

import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.combine.Combine;
import net.aegistudio.transparentx.combine.NomodifyRedundant;

public class TextureMapping implements ShaderEffectClass {
	private final int order;
	private final Combine combining;
	private final TreeSet<String> targetVariable;
	
	public TextureMapping(String targetVariable, int order, Combine combining) {
		this(order, combining, targetVariable);
	}
	
	public TextureMapping(int order, Combine combining, String... mutateds) {
		this.order = order;
		this.combining = combining;
		this.targetVariable = new TreeSet<>();
		for(String mutated : mutateds)
			this.targetVariable.add(mutated);
	}
	
	@Override
	public double getPriority() {
		return 1.0 + order;
	}

	@Override
	public Combine getCombine(String mutatedVariable) {
		if(targetVariable.contains(mutatedVariable))
			return combining;
		return new NomodifyRedundant(1);
	}
}
