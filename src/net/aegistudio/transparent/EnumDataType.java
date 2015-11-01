package net.aegistudio.transparent;

import org.lwjgl.opengl.GL11;

public enum EnumDataType implements Constant{

	BYTE(GL11.GL_BYTE, Byte.SIZE / 8),
	SHORT(GL11.GL_SHORT, Short.SIZE / Byte.SIZE),
	INT(GL11.GL_INT, Integer.SIZE / Byte.SIZE),
	FLOAT(GL11.GL_FLOAT, Float.SIZE / Byte.SIZE),
	DOUBLE(GL11.GL_DOUBLE, Double.SIZE / Byte.SIZE);
	
	public final int dataTypeId;
	public final int sizeof;
	
	private EnumDataType(int dataTypeId, int sizeof) {
		this.dataTypeId = dataTypeId;
		this.sizeof = sizeof;
	}
	
	@Override
	public int getValue() {
		return this.dataTypeId;
	}
	
	public int sizeof() {
		return this.sizeof;
	}
}
