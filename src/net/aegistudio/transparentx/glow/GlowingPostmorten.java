package net.aegistudio.transparentx.glow;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparentx.lang.EnumModifier;
import net.aegistudio.transparentx.lang.SharingVariable;
import net.aegistudio.transparentx.lang.Symbol;
import net.aegistudio.transparentx.prog.PostmortenWorker;

public class GlowingPostmorten extends PostmortenWorker{
	TreeMap<String, Symbol> globals = new TreeMap<String, Symbol>();
	TreeSet<String> normalProcessVariable = new TreeSet<String>();
	public GlowingPostmorten() {
		globals.put("_glowingMapColor", new Symbol("_glowingMapColor", "vec4", EnumModifier.NONE, null, null));
		new SharingVariable("_glowingMapColor", "vec4", EnumShaderType.FRAGMENT, "_glowingMapColor=vec4(0.0);").submit();
		normalProcessVariable.add("_glowingMapColor");
	}
	
	@Override
	public Map<String, Symbol> globalVariables() {
		return globals;
	}

	@Override
	public String callbackBlock() {
		return "gl_FragColor = _glowingMapColor;";
	}

	@Override
	public Set<String> normalProcessVariable() {
		return normalProcessVariable;
	}

}
