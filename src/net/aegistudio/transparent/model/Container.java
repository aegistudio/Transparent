package net.aegistudio.transparent.model;

public class Container implements Drawable {

	private final ScopeManager<Drawable> manager = new ScopeManager<Drawable>() {

		@Override
		protected void doCreate(Drawable t) throws Exception {
			t.create();
		}

		@Override
		protected void doDestroy(Drawable t) throws Exception{
			t.destroy();
		}
		
	};
	
	@Override
	public void create() throws Exception {
		manager.create();
	}
	
	@Override
	public synchronized void render() throws Exception {
		for(Drawable drawable : manager.beforeRender()) 
			drawable.render();
	}

	@Override
	public synchronized void destroy() throws Exception {
		manager.destroy();
	}
	
	public synchronized void addDrawable(Drawable drawable) {
		manager.add(drawable);
	}
	
	public synchronized void removeDrawable(Drawable drawable) {
		manager.remove(drawable);
	}
}
