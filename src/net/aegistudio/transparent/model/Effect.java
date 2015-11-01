package net.aegistudio.transparent.model;

public interface Effect {
	/**
	 * When this effect added to the model and needs to be
	 * initialized, will call this method.
	 * @throws Exception 
	 */
	public void create() throws Exception;
	
	/**
	 * When this effect gets in use, will call this method.
	 */
	public void use() throws Exception;
	
	/**
	 * When this effect gets out of use, will call this method.
	 */
	public void recover() throws Exception;
	
	/**
	 * When this effect permanently remove from model and needs
	 * an de-constructor, will call this method.
	 */
	public void destroy() throws Exception;
}
