package net.aegistudio.transparent.mvp;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import net.aegistudio.transparent.model.Effect;

/**
 * This effect represents projection transform.
 * 
 * @author aegistudio
 */

public abstract class Projection implements Effect {

	protected final Transform viewTransform = new Transform();
	
	public void setPosition(double x, double y, double z) {
		this.viewTransform.setPosition(-x, -y, -z);
	}
	
	public void setRotation(double rzX, double rzY, double rzZ,
			double rxX, double rxY, double rxZ) {
		this.viewTransform.setRotation(rzX, rzY, rzZ, rxX, rxY, rxZ);
	}
	
	public void create() {
		
	}
	
	public void use() {
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glTranslated(this.viewTransform.translationX,
				this.viewTransform.translationY,
				this.viewTransform.translationZ);
		GL13.glMultTransposeMatrix(this.viewTransform.rotation);
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		projection();
	}
	
	public void recover() {
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
	}
	
	protected abstract void projection();
	
	public void destroy() {
		
	}
}
