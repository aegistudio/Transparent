package net.aegistudio.transparent.model;

/**
 * Scene = ShaderEffect + Effect + Container
 * @author aegistudio
 */

public class Scene extends Entity{
	private final Container container;
	
	public Scene() {
		super(new Container());
		this.container = (Container) super.wrapped;
	}
	
	public void add(Drawable drawable) {
		this.container.addDrawable(drawable);
	}
	
	public void remove(Drawable drawable) {
		this.container.removeDrawable(drawable);
	}
}
