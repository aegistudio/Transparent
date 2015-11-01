package net.aegistudio.transparent.model;

import java.util.ArrayList;
import java.util.List;

public abstract class ScopeManager<T> {
	protected final ArrayList<T> initializing = new ArrayList<T>();
	protected final ArrayList<T> rendering = new ArrayList<T>();
	protected final ArrayList<T> removing = new ArrayList<T>();
	
	public void add(T t) {
		synchronized (this.initializing) {
			initializing.add(t);	
		}
	}
	
	public void remove(T t) {
		synchronized(this.removing) {
			removing.add(t);
		}
	}
	
	public void create() throws Exception {
		this.rendering.clear();
		this.add();
		synchronized(this) {
			this.removing.clear();
		}
	}
	
	public void add() throws Exception {
		synchronized(this.initializing) {
			synchronized(this.removing) {
				initializing.removeAll(removing);
				removing.removeAll(initializing);
			}
			
			for(T t : initializing){
				doCreate(t);
				rendering.add(t);	
			}
			initializing.clear();
		}
	}
	
	public void remove() throws Exception {
		synchronized(this.removing) {
			synchronized(this.initializing) {
				removing.removeAll(initializing);
				initializing.removeAll(removing);
			}
			
			for(T t : removing) {
				doDestroy(t);
				rendering.remove(t);
			}
			removing.clear();
		}
	}
	
	public void destroy() throws Exception {
		synchronized(this.initializing) {
			this.initializing.clear();
		}
		this.remove();
		for(T t : rendering) doDestroy(t);
		rendering.clear();
	}
	
	public List<T> beforeRender() throws Exception{
		this.add();
		this.remove();
		return this.rendering;
	}
	
	protected abstract void doCreate(T t) throws Exception;
	protected abstract void doDestroy(T t) throws Exception;
}
