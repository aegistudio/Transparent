package net.aegistudio.transparent.model;

/**
 * Drawable is the basic idea of modeling. Which is a 
 * leaf or a subtree (by a subtree root node) when
 * constructing the drawable model.
 * 
 * @author aegistudio
 */

public interface Drawable {
	public void create() throws Exception;
	
	public void render() throws Exception;
	
	public void destroy() throws Exception;
}
