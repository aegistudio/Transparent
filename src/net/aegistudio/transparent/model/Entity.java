package net.aegistudio.transparent.model;

import net.aegistudio.transparentx.ShaderEffect;
import net.aegistudio.transparentx.ShaderStrip;

/**
 * Scene = ShaderEffect + Effect + Drawable
 * @author aegistudio
 */

public class Entity implements Drawable {
	protected final EffectBank effectBank;
	protected final Drawable wrapped;
	protected final ShaderStrip shaderStrip;
	
	public Entity(Drawable wrapped) {
		this.effectBank = new EffectBank();
		this.wrapped = wrapped;
		this.shaderStrip = new ShaderStrip();
	}
	
	@Override
	public void create() throws Exception {
		this.effectBank.create();
		this.wrapped.create();
		this.shaderStrip.create();
	}

	@Override
	public void render() throws Exception {
		this.effectBank.use();
		this.shaderStrip.push(this.wrapped);
		this.wrapped.render();
		this.shaderStrip.pop(this.wrapped);
		this.effectBank.recover();
	}

	@Override
	public void destroy() throws Exception {
		this.effectBank.destroy();
		this.wrapped.destroy();
		this.shaderStrip.destroy();
	}
	
	public void addEffect(Effect fx) {
		this.effectBank.addEffect(fx);
	}
	
	public void addEffect(ShaderEffect sfx) {
		this.shaderStrip.addEffect(sfx);
	}
	
	public void removeEffect(Effect fx) {
		this.effectBank.removeEffect(fx);
	}
	
	public void removeEffect(ShaderEffect sfx) {
		this.shaderStrip.removeEffect(sfx);
	}
}
