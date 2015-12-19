package net.aegistudio.transparent.hint;

import org.lwjgl.opengl.GL11;

import net.aegistudio.transparent.Constant;

public enum EnumActivable implements Activable, Constant{
	ALPHA_TEST(GL11.GL_ALPHA_TEST),
	DEPTH_TEST(GL11.GL_DEPTH_TEST),
	LIGHTING(GL11.GL_LIGHTING);
	
	private final int hintId;
	private EnumActivable(int hintId) {
		this.hintId = hintId;
	}
	
	@Override
	public int getValue() {
		return this.hintId;
	}

	public boolean hasActivated() {
		return GL11.glGetBoolean(hintId);
	}
	
	public void activate() {
		GL11.glEnable(hintId);
	}
	
	public void deactivate() {
		GL11.glDisable(hintId);
	}
}
