package net.aegistudio.transparentx.shadow;

import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.combine.Combine;

public class Shadow implements ShaderEffectClass {

	@Override
	public double getPriority() {
		return 0;
	}

	@Override
	public Combine getCombine(String mutatedVariable) {
		// TODO Auto-generated method stub
		return null;
	}

}
