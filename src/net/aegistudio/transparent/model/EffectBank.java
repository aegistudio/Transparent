package net.aegistudio.transparent.model;

public class EffectBank implements Effect {
	
	private final ScopeManager<Effect> manager = new ScopeManager<Effect>() {

		@Override
		protected void doCreate(Effect t) throws Exception {
			t.create();
		}

		@Override
		protected void doDestroy(Effect t) throws Exception {
			t.destroy();
		}
		
	};
	public void addEffect(Effect fx){
		manager.add(fx);
	}
	
	public void removeEffect(Effect fx){
		manager.remove(fx);
	}

	@Override
	public void create() throws Exception{
		manager.create();
	}

	@Override
	public void use() throws Exception {
		for(Effect fx : manager.beforeRender())
			fx.use();
	}

	@Override
	public void recover() throws Exception {
		for(Effect fx : manager.beforeRender())
			fx.recover();
	}

	@Override
	public void destroy() throws Exception {
		manager.destroy();
	}
}
