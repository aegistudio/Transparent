package net.aegistudio.transparentx.ao;

import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.combine.Combine;
import net.aegistudio.transparentx.combine.ModifyReplaceStreamed;

public class AmbientOcclusion implements ShaderEffectClass{
	@Override
	public double getPriority() {
		return 1001;
	}

	@Override
	public Combine getCombine(String mutatedVariable) {
		return new ModifyReplaceStreamed(0);
	}
}
