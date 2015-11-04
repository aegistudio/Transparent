package net.aegistudio.transparentx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Shader strip is rather an accumulator than
 * an actual shading program. It collects all
 * shader effects and combine it to form a shader
 * program.
 * 
 * Please notice that adding a shader effect of
 * the same class will replace the effect in this
 * strip.
 * 
 * There's a global shader strip stack that traces 
 * the activating effects.
 * 
 * @author aegistudio
 */

public class ShaderStrip {
	// ----------- GLOBAL TABLES -----------
	private static final Map<Double, ShaderEffect> activeEffects
		= new HashMap<Double, ShaderEffect>();

	private static final Map<Double, ShaderEnable> activeEffectsEnable
		= new HashMap<Double, ShaderEnable>();
	
	private static ShaderEffectProgram sfxProgram;
	
	private static final Stack<Map<Double, ShaderEffect>> replacementStack
		= new Stack<Map<Double, ShaderEffect>>();
	private static final Stack<Map<Double, ShaderEnable>> replacementEnableStack
		= new Stack<Map<Double, ShaderEnable>>();
	
	// ------------ LOCAL TABLES ------------
	private final List<ShaderEffect> addingEffects
		= new ArrayList<ShaderEffect>();
	
	private final Map<Double, ShaderEffect> effects
		= new HashMap<Double, ShaderEffect>();
	private final Map<Double, ShaderEnable> effectEnable
		= new HashMap<Double, ShaderEnable>();
	
	private final List<ShaderEffect> removingEffects
		= new ArrayList<ShaderEffect>();
	// --------------------------------------
	
	public void addEffect(ShaderEffect sfx){
		synchronized(addingEffects) {
			addingEffects.add(sfx);
		}
	}
	
	public void removeEffect(ShaderEffect sfx){
		synchronized(removingEffects) {
			removingEffects.add(sfx);
		}
	}
	
	/**
	 * Update local table entries, add not-in-table entries, replace 
	 * entries of the same priority and remove in-table entries.
	 */
	
	protected void localEntryUpdate(){ 
		synchronized (addingEffects) {
			for(ShaderEffect addingEntry : addingEffects) {
				double priority = addingEntry.getShaderEffectClass().getPriority();
				effects.put(priority, addingEntry);
				effectEnable.put(priority, new ShaderEnable(addingEntry));
			}
			addingEffects.clear();
		}
		synchronized (removingEffects) {
			for(ShaderEffect removingEntry : removingEffects) {
				if(effects.remove(removingEntry.getShaderEffectClass().getPriority(),
						removingEntry))
					effectEnable.remove(removingEntry.getShaderEffectClass().getPriority());
			}
			removingEffects.clear();
		}
	}
	
	protected void initShaderEffectProgram() {
		if(sfxProgram == null) {
			sfxProgram = new ShaderEffectProgram();
			sfxProgram.create();
		}
	}
	
	public void push() throws Exception {
		this.localEntryUpdate();
		this.initShaderEffectProgram();
		
		// Do nothing when effects table is currently empty.
		if(this.effects.isEmpty()) return;
		if(activeEffects.isEmpty()) 
			sfxProgram.pushShaderProgram();
		
		Map<Double, ShaderEffect> replacement = new HashMap<Double, ShaderEffect>();
		Map<Double, ShaderEnable> replacementEnable = new HashMap<Double, ShaderEnable>();
		
		boolean newlyAdded = false;
		for(ShaderEffect sfx : effects.values()) 
			if(sfxProgram.adapt(sfx)) newlyAdded = true;
		if(newlyAdded) sfxProgram.recompile();
		
		for(Double priority : effects.keySet()) {
			ShaderEffect sfx = effects.get(priority);
			ShaderEnable sfxEnable = effectEnable.get(priority);
			sfxProgram.adapt(sfx);
			sfxEnable.enable();
			sfx.setParameters();
			
			if(activeEffects.containsKey(priority))
					if(activeEffects.get(priority) != sfx) {
				activeEffectsEnable.get(priority).disable();
				replacement.put(priority, activeEffects.get(priority));
				replacementEnable.put(priority, activeEffectsEnable.get(priority));
			}
			activeEffects.put(priority, sfx);
			activeEffectsEnable.put(priority, sfxEnable);
		}
		
		replacementStack.push(replacement);
		replacementEnableStack.push(replacementEnable);
	}
	
	public void pop() {
		this.initShaderEffectProgram();
		
		if(!this.effects.isEmpty()) {
			for(Double priority : effects.keySet()) {
				effectEnable.get(priority).disable();
				activeEffects.remove(priority);
				activeEffectsEnable.remove(priority);
			}
	
			Map<Double, ShaderEffect> replacement = replacementStack.pop();
			Map<Double, ShaderEnable> replacementEnable = replacementEnableStack.pop();
			
			for(Double priority : replacement.keySet()) {
				ShaderEffect sfx = replacement.get(priority);
				ShaderEnable sfxEnable = replacementEnable.get(priority);
				
				sfxEnable.enable();
				sfx.setParameters();
	
				activeEffects.put(priority, sfx);
				activeEffectsEnable.put(priority, sfxEnable);
			}
			
			if(activeEffects.isEmpty()) 
				sfxProgram.popShaderProgram();
		}
		
		this.localEntryUpdate();
	}
}