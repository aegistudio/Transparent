package net.aegistudio.transparent.mvp;

import java.nio.DoubleBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import net.aegistudio.transparent.model.Effect;

/**
 * This effect represents modelview transform.
 * 
 * When this effect activates the transform of a leaf (an entity),
 * it represents the coordinate of the entity related to a scene.
 * 
 * When this effect activates the transform of a subtree node (a
 * scene), it represents the coordinate of the scene related to its
 * container scene.
 * 
 * The model view transform contains these transforms: translation,
 * rotation, scaling. And these transform will first activate the 
 * scaling, then the rotation, and finally the translation.
 * 
 * @author aegistudio
 */

public class Transform implements Effect {
	public Transform() {
		rotation = BufferUtils.createDoubleBuffer(16);
		rotation.put(new double[]{
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1});
		rotation.flip();
	}
	
	double translationX = 0,
				translationY = 0,
				translationZ = 0;
	
	public void setPosition(double x, double y, double z){
		this.translationX = x;
		this.translationY = y;
		this.translationZ = z;
	}
	
	double scaleX = 1,
			scaleY = 1,
			scaleZ = 1;
	
	public void setScale(double ampX, double ampY, double ampZ) {
		this.scaleX = ampX;
		this.scaleY = ampY;
		this.scaleZ = ampZ;
	}
	
	DoubleBuffer rotation;
	
	public void setRotation(double rotzX, double rotzY, double rotzZ,
			double rotxX, double rotxY, double rotxZ) {
		double modulusZ = Math.sqrt(rotzX * rotzX + rotzY * rotzY + rotzZ * rotzZ);
		double modulusX = Math.sqrt(rotxX * rotxX + rotxY * rotxY + rotxZ * rotxZ);
		
		rotzX /= modulusZ; rotzY /= modulusZ; rotzZ /= modulusZ;
		rotxX /= modulusX; rotxY /= modulusX; rotxZ /= modulusX;
		
		double cosalpha = rotzX * rotxX + rotzY * rotxY + rotzZ * rotxZ;
		double sinalpha = Math.sqrt(1 - cosalpha * cosalpha);
		
		rotxX += cosalpha * rotzX; rotxY += cosalpha * rotzY; rotxZ += cosalpha * rotzZ;
		rotxX /= sinalpha; rotxY /= sinalpha; rotxZ /= sinalpha;
		
		double rotyX = rotxY * rotzZ - rotxZ * rotzY;
		double rotyY = rotxZ * rotzX - rotxX * rotzZ;
		double rotyZ = rotxX * rotzY - rotxY * rotzX;
		
		DoubleBuffer newRotation = BufferUtils.createDoubleBuffer(16);
		newRotation.put(new double[]{
				rotxX, rotxY, rotxZ, 0,
				rotyX, rotyY, rotyZ, 0,
				rotzX, rotzY, rotzZ, 0,
				0,	0,	0,	1});
		newRotation.flip();
		rotation = newRotation;
	}
	
	@Override
	public void create() { }

	@Override
	public void use() {
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		
		GL11.glTranslated(translationX, translationY, translationZ);
		GL11.glMultMatrix(rotation);
		GL11.glScaled(scaleX, scaleY, scaleZ);
	}

	@Override
	public void recover() {
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();
	}

	@Override
	public void destroy() {	}
}
