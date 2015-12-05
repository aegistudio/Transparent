package net.aegistudio.transparentx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import net.aegistudio.transparent.model.Drawable;
import net.aegistudio.transparent.model.ScopeManager;

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
	
	static DefaultEffectProgram defaultProgram;
	
	private static final Stack<Map<Double, ShaderEffect>> replacementStack
		= new Stack<Map<Double, ShaderEffect>>();
	private static final Stack<Map<Double, ShaderEnable>> replacementEnableStack
		= new Stack<Map<Double, ShaderEnable>>();
	//------------------------------------------
	
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
	
	// ------------ COMPLEX CONTROLLERS ------------
	private final ScopeManager<ComplexEffect> complexEffect = new ScopeManager<ComplexEffect>() {
		@Override
		protected void doCreate(ComplexEffect t) throws Exception {
			t.create();
		}

		@Override
		protected void doDestroy(ComplexEffect t) throws Exception {
			t.destroy();
		}
	};
	
	static boolean normal = true;									// Inside normal process.
	static ShaderEffectProgram sfxProgram;							// Using program.
	
	void reactivate() throws Exception {
		if(sfxProgram != defaultProgram) {
			boolean newlyAdded = false;
			for(ShaderEffect sfx : activeEffects.values()) 
				if(sfxProgram.adapt(sfx)) newlyAdded = true;
			if(newlyAdded) sfxProgram.recompile();
		}
		if(sfxProgram != null)
			for(Double priorities : activeEffects.keySet()) {
				ShaderEffect shaderEffect = activeEffects.get(priorities);
				if(!normal && (shaderEffect instanceof ComplexEffect)) 
					if(complexRender.shouldBypass(shaderEffect)) continue;
				activeEffectsEnable.get(priorities).enable();
				shaderEffect.setParameters();
			}
	}
	// ---------------------------------------------
	
	public void addEffect(ShaderEffect sfx){
		synchronized(addingEffects) {
			addingEffects.add(sfx);
			if(sfx instanceof ComplexEffect) 
				complexEffect.add((ComplexEffect) sfx);
		}
	}
	
	public void removeEffect(ShaderEffect sfx){
		synchronized(removingEffects) {
			removingEffects.add(sfx);
			if(sfx instanceof ComplexEffect) 
				complexEffect.remove((ComplexEffect) sfx);
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
		if(defaultProgram == null) {
			defaultProgram = new DefaultEffectProgram();
			defaultProgram.create();
			sfxProgram = defaultProgram;
		}
	}
	
	private ComplexRender complexRender = new ComplexRender(this);
	
	public void push(Drawable itself) throws Exception {
		this.localEntryUpdate();
		this.initShaderEffectProgram();
		
		// 0. do not do redundant things when null is designated.
		if(!normal && (sfxProgram == null)) return;
		
		// 1. Check given effects, do nothing if effects table is currently empty.
		if(this.effects.isEmpty()) return;
		
		// 2. If its the first time to adsorb effects, push the default shader program.
		if(normal && activeEffects.isEmpty()) 
				defaultProgram.pushShaderProgram();
		
		// 3. Adapt newly assigned shader effects, and recompile shader program.
		boolean newlyAdded = false;
		for(ShaderEffect sfx : effects.values()) 
			if(sfxProgram.adapt(sfx)) newlyAdded = true;
		if(newlyAdded) sfxProgram.recompile();
		
		// 5. Do prerender. (Before parameter setting)
		if(normal) {
			List<ComplexEffect> complexEffects = this.complexEffect.beforeRender();
			if(complexEffects != null) {
				complexRender.setScene(itself);
				normal = false;
				for(ComplexEffect complexEffect : complexEffects) {
					complexRender.resetDefault();
					complexRender.setCurrentEffect(complexEffect);
					complexEffect.prerender(complexRender);
				}
				normal = true;
			}
		}
		
		// 4. Enable effects and replace conflicted effects.
		Map<Double, ShaderEffect> replacement = new HashMap<Double, ShaderEffect>();
		Map<Double, ShaderEnable> replacementEnable = new HashMap<Double, ShaderEnable>();
		
		for(Double priority : effects.keySet()) {
			ShaderEffect sfx = effects.get(priority);
			if(!normal) if(complexRender.shouldBypass(sfx)) continue;
			
			ShaderEnable sfxEnable = effectEnable.get(priority);
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
	
	public void pop(Drawable itself) throws Exception {
		this.initShaderEffectProgram();
		
		// 0. do not do redundant things when null is designated.
		if(!normal && (sfxProgram == null)) return;
		
		// 1. Check given effects, do nothing if effects table is currently empty.
		// (Please caution that the local entry is not updated.)
		if(!this.effects.isEmpty()) {
			
			// 2. Disable effects.
			for(Double priority : effects.keySet()) {
				effectEnable.get(priority).disable();
				activeEffects.remove(priority);
				activeEffectsEnable.remove(priority);
			}
	
			// 3. Recover replaced effects.
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
			
			// 4. Do postrender.
			if(normal) {
				List<ComplexEffect> complexEffects = this.complexEffect.beforeRender();
				if(complexEffects != null) {
					complexRender.setScene(itself);
					normal = false;
					for(ComplexEffect complexEffect : complexEffects) {
						complexRender.resetDefault();
						complexRender.setCurrentEffect(complexEffect);
						complexEffect.postrender(complexRender);
					}
					normal = true;
				}
			}
			
			// 5. If all effects are popped, disable default process program.
			if(normal && activeEffects.isEmpty()) 
				defaultProgram.popShaderProgram();
		}
		
		this.localEntryUpdate();
	}
	
	public void create() {
		try {
			this.complexEffect.create();
		}
		catch(Exception e) {
			if(e instanceof RuntimeException) throw (RuntimeException)e;
		}
	}
	
	public void destroy() {
		try {
			this.complexEffect.destroy();
		} catch (Exception e) {
			if(e instanceof RuntimeException) throw (RuntimeException)e;
		}
	}
}