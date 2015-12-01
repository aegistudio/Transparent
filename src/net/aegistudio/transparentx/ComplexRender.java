package net.aegistudio.transparentx;

import java.util.Comparator;
import java.util.HashMap;

import net.aegistudio.transparent.model.Drawable;

/**
 * Passed in as a controller for pre-render.
 * Able to update the state, shader program, etc.
 * 
 * @author aegistudio
 */

public class ComplexRender {
	private static HashMap<Class<? extends ShaderEffectProgram>, ShaderEffectProgram> effectPrograms
		= new HashMap<Class<? extends ShaderEffectProgram>, ShaderEffectProgram>();
	
	Drawable scene;
	void setScene(Drawable scene) {
		this.scene = scene;
	}

	// All will be reset except for scene.
	void resetDefault() {
		this.bypass = true;
		this.classBypass = false;
		this.comparator = null;
	}
	
	/**
	 * Effects itself will be disabled.
	 * Default true.
	 */
	boolean bypass;
	public void setBypass(boolean bypass) {
		this.bypass = bypass;
	}
	
	/** 
	 * Effects belongs to the same shader class will 
	 * be bypassed / not disabled. 
	 * Default false;
	 */

	boolean classBypass;
	public void setClassBypass(boolean classBypass) {
		this.classBypass = classBypass;
	}
	
	Comparator<ShaderEffect> comparator;
	public void setBypassComparator(Comparator<ShaderEffect> comparator) {
		this.comparator = comparator;
	}
	
	/**
	 * Will render itself however under complex render context.
	 * 
	 * programClass == null : use fixed pipeline, and no shader effect will be 
	 * used no matter what is being specified in shader strips.
	 * programClass == DefaultEffectProgram : uses default rendering program.
	 * programClass == P extends ShaderEffectProgram : uses specified program.
	 * 
	 * @param programClass
	 * @throws Exception 
	 */
	
	public void self(Class<? extends ShaderEffectProgram> programClass) throws Exception {
		if(programClass == null) {
			ShaderStrip.sfxProgram = null;
			ShaderStrip.defaultProgram.pop();
			this.scene.render();
			ShaderStrip.defaultProgram.push();
			ShaderStrip.sfxProgram = ShaderStrip.defaultProgram;
			ShaderStrip.reactivate();
		}
		else if(programClass == DefaultEffectProgram.class) {
			ShaderStrip.sfxProgram = ShaderStrip.defaultProgram;
			this.scene.render();
		}
		else {
			ShaderStrip.sfxProgram = effectPrograms.get(programClass);
			if(ShaderStrip.sfxProgram == null) {
				ShaderStrip.sfxProgram = programClass.newInstance();
				effectPrograms.put(programClass, ShaderStrip.sfxProgram);
			}
			ShaderStrip.sfxProgram.push();
			ShaderStrip.reactivate();
			this.scene.render();
			ShaderStrip.sfxProgram.pop();
			ShaderStrip.sfxProgram = ShaderStrip.defaultProgram;
			ShaderStrip.reactivate();
		}
	}
	
	ComplexEffect shaderEffect;
	void setCurrentEffect(ComplexEffect shaderEffect) {
		this.shaderEffect = shaderEffect;
	}
	
	boolean shouldBypass(ShaderEffect fx) {
		if(comparator != null) 
			return comparator.compare(shaderEffect, fx) == 0;
		else if(bypass) {
			if(shaderEffect == fx) return true;
			else if(shaderEffect.getShaderEffectClass().getClass()
					== fx.getShaderEffectClass().getClass()) return true;
		}
		return false;
	}
}
