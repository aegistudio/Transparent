package net.aegistudio.transparent.image;

public class UnityTranslator implements Translator {
	private double r, g, b, a;
	@Override
	public void translate(double r, double g, double b, double a) {
		this.r = r; this.g = g; this.b = b; this.a = a;
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
