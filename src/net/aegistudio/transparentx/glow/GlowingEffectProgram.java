package net.aegistudio.transparentx.glow;

import java.util.List;
import java.util.Map;

import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparentx.DefaultEffectProgram;
import net.aegistudio.transparentx.ShaderEffect;
import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.light.Lighting;
import net.aegistudio.transparentx.prog.SubprogramWorker;
import net.aegistudio.transparentx.shadow.Shadow;

public class GlowingEffectProgram extends DefaultEffectProgram {
	public GlowingEffectProgram() {
		super();
		Map<Double, List<SubprogramWorker>> map = this.createSubprogramWorkerMap();
		List<SubprogramWorker> list = this.createSubprogramWorkerList();
		list.add(new GlowingPostmorten());
		map.put(99999.0, list);
		super.subprogramWorker.put(EnumShaderType.FRAGMENT, map);
	}
	
	boolean hasCompiledOnce = false;
	public boolean adapt(ShaderEffect sfx) throws Exception {
		if(!(sfx instanceof GlowingSubEffect)) {
			ShaderEffectClass shaderEffectClass = sfx.getShaderEffectClass();
			if(shaderEffectClass instanceof Lighting) return false;
			if(shaderEffectClass instanceof Shadow) return false;
			return super.adapt(sfx);
		}
		else {
			((GlowingSubEffect)sfx).setGlowingStrip(true);
			boolean adapted = super.adapt(sfx);
			((GlowingSubEffect)sfx).setGlowingStrip(false);
			return adapted;
		}
	}
	
	public void create() {
		super.create();
		try {
			recompile();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public String constructMainCode(EnumShaderType shaderType, Map<Double, List<SubprogramWorker>> subprogramMap) {
		String main = super.constructMainCode(shaderType, subprogramMap);
		return main;
	}
}
