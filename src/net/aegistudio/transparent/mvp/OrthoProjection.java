package net.aegistudio.transparent.mvp;

import org.lwjgl.opengl.GL11;

public class OrthoProjection extends Projection {
	double left, right, top, bottom, zNear, zFar;
	
	public OrthoProjection(double left, double right, double bottom,
			double top, double zNear, double zFar) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
		this.zNear = zNear;
		this.zFar = zFar;
	}

	public OrthoProjection(double horizontal, double vertical, double depth) {
		this(-horizontal / 2, horizontal / 2, -vertical / 2, 
				vertical / 2, depth / 2, -depth / 2);
	}
	
	@Override
	protected void projection() {
		GL11.glOrtho(left, right, bottom, top, zNear, zFar);
	}
}
