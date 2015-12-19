package net.aegistudio.transparentx.glow;

import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.combine.Combine;
import net.aegistudio.transparentx.combine.ModifyReplaceOriginal;

public class GlowEffect implements ShaderEffectClass {

	@Override
	public double getPriority() {
		return 1001;
	}

	@Override
	public Combine getCombine(String mutatedVariable) {
		if(mutatedVariable.equals("gl_FragColor")) return new ModifyReplaceOriginal();
		else return null;
	}
}
