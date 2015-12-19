package net.aegistudio.transparentx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparent.shader.ShaderObject;
import net.aegistudio.transparentx.prog.AbstractEffectProgram;
import net.aegistudio.transparentx.prog.StandardWorker;
import net.aegistudio.transparentx.prog.SubprogramWorker;

public class DefaultEffectProgram extends AbstractEffectProgram {
	public DefaultEffectProgram() {
		super(new TreeSet<String>(), new TreeMap<EnumShaderType, ShaderObject>(),
				new TreeMap<EnumShaderType, Map<Double, List<SubprogramWorker>>>());
	}

	@Override
	protected Map<Double, List<SubprogramWorker>> createSubprogramWorkerMap() {
		return new TreeMap<Double, List<SubprogramWorker>>();
	}

	@Override
	protected List<SubprogramWorker> createSubprogramWorkerList() {
		return new ArrayList<SubprogramWorker>();
	}

	@Override
	protected SubprogramWorker newSubprogramWorker(EnumShaderType shaderType, ShaderEffectClass sfxClazz, String prefix,
			String[] sourceCode) {
		return new StandardWorker(shaderType, sfxClazz, prefix, sourceCode);
	}
	
}
