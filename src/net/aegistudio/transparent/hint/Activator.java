package net.aegistudio.transparent.hint;

import net.aegistudio.transparent.model.Effect;

public class Activator implements Effect {
	private final Activable activable;
	private final boolean activate;
	public Activator(Activable activable, boolean activate) {
		this.activable = activable;
		this.activate = activate;
	}

	@Override
	public void create() throws Exception {
		
	}
	
	boolean previousStatus;
	@Override
	public void use() throws Exception {
		previousStatus = activable.hasActivated();
		if(previousStatus != activate) {
			if(activate) activable.activate();
			else activable.deactivate();
		}
	}

	@Override
	public void recover() throws Exception {
		if(previousStatus != activable.hasActivated()) {
			if(previousStatus) activable.activate();
			else activable.deactivate();
		}
	}

	@Override
	public void destroy() throws Exception {
		
	}
}
