package net.aegistudio.transparent;

import org.lwjgl.opengl.GL11;

public enum EnumPrimitive implements Constant{
	POINTS(GL11.GL_POINTS),
	
	LINES(GL11.GL_LINES),
	LINE_STRIP(GL11.GL_LINE_STRIP),
	LINE_LOOP(GL11.GL_LINE_LOOP),
	
	TRIANGLES(GL11.GL_TRIANGLES),
	TRIANGLE_STRIP(GL11.GL_TRIANGLE_STRIP),
	TRIANGLE_FAN(GL11.GL_TRIANGLE_FAN),
	
	QUADS(GL11.GL_QUADS),
	QUAD_STRIP(GL11.GL_QUAD_STRIP),
	
	POLYGON(GL11.GL_POLYGON);
	
	private final int primitiveId;
	
	private EnumPrimitive(int stateId) {
		this.primitiveId = stateId;
	}

	@Override
	public int getValue() {
		return primitiveId;
	}
}
