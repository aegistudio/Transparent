package net.aegistudio.transparentx;

/**
 * Any effect that needs to be prerendered will be regarded as a complex effect.
 * Complex effect is able to initialize itself like a effect.
 * 
 * @author aegistudio
 */

public interface ComplexEffect extends ShaderEffect {
	public void create();
	
	public void prerender(ComplexRender controller) throws Exception;
	
	public void postrender(ComplexRender controller) throws Exception;
	
	public void destroy();
}
