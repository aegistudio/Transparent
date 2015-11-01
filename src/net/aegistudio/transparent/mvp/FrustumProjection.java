package net.aegistudio.transparent.mvp;

import org.lwjgl.opengl.GL11;

public class FrustumProjection extends Projection {

	double left, right, top, bottom, zNear, zFar;
	
	public FrustumProjection(double left, double right, double bottom,
			double top, double zNear, double zFar) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
		this.zNear = zNear;
		this.zFar = zFar;
	}
	
	public FrustumProjection(double width, double height, double depthBegin, double depthEnd) {
		this(-width/2, width/2, -height/2, height/2, depthBegin, depthEnd);
	}
	
	@Override
	protected void projection() {
		GL11.glFrustum(left, right, bottom, top, zNear, zFar);
	}
}
