package net.aegistudio.transparent;

import org.lwjgl.opengl.GL11;

public abstract class DisplayList {
	protected int displayListId = 0;
	
	// Will recompile and execute when set to dirty.
	protected boolean dirty = false;
	
	public void create() {
		if(displayListId == 0) {
			displayListId = GL11.glGenLists(1);
			if(displayListId == 0) throw new UnallocatableException(this);
			GL11.glNewList(displayListId, GL11.GL_COMPILE);
			this.call();
			GL11.glEndList();
			dirty = false;
		}
	}
	
	protected abstract void call();
	
	public void use() {
		if(displayListId == 0)
			throw new UninitializedException(this);
		if(dirty) {
			GL11.glNewList(displayListId, GL11.GL_COMPILE_AND_EXECUTE);
			this.call();
			GL11.glEndList();
			dirty = false;
		}
		else GL11.glCallList(displayListId);
	}
	
	/**
	 * Call it to make the display list dirty.
	 */
	public void markDirty() {
		if(displayListId != 0) dirty = true;
	}
	
	public void destroy() {
		if(displayListId != 0) {
			GL11.glDeleteLists(displayListId, 1);
			displayListId = 0;
			dirty = false;
		}
	}
}
