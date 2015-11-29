package net.aegistudio.transparent.image;

/**
 * Translator could do preprocessing to the given image at pixel level.
 * @author aegistudio
 */

public interface Translator {
	public void translate(double r, double g, double b, double a);
	
	public double getRedComponent();
	
	public double getGreenComponent();
	
	public double getBlueComponent();
	
	public double getAlphaComponent();
}
