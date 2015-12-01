package net.aegistudio.transparentx;

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
	
	private final Drawable scene;
	
	ComplexRender(Drawable scene) {
		this.scene = scene;
	}
	
	/** 
	 * Effects belongs to the same shader class will 
	 * be bypassed / not disabled. 
	 */
	public void setClassBypass(boolean classBypass) {
		
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
}
