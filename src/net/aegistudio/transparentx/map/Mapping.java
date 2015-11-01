package net.aegistudio.transparentx.map;

import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.combine.Combine;

public class Mapping implements ShaderEffectClass {
	
	@Override
	public double getPriority() {
		return 0;
	}

	@Override
	public Combine getCombine(String mutatedVariable) {
		return null;
	}

}
