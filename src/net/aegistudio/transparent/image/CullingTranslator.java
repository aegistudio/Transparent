package net.aegistudio.transparent.image;

/**
 * Culling translator will attenuate corresponding color in alpha.
 * @author aegistudio
 */

public class CullingTranslator implements Translator{
	private final double cullr, cullg, cullb;
	private double r, g, b, a;
	
	public CullingTranslator(double cullr, double cullg, double cullb) {
		this.cullr = cullr; this.cullg = cullg; this.cullb = cullb;
	}

	@Override
	public void translate(double r, double g, double b, double a) {
		if(r <= cullr && g <= cullg && b <= cullb)
			this.r = this.g = this.b = this.a = 0;
		else {
			this.r = r;
			this.g = g;
			this.b = b;
			this.a = a;
		}
	}

	@Override
	public double getRedComponent() {
		return r;
	}

	@Override
	public double getGreenComponent() {
		return g;
	}

	@Override
	public double getBlueComponent() {
		return b;
	}

	@Override
	public double getAlphaComponent() {
		return a;
	}
}
