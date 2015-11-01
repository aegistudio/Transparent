package net.aegistudio.transparent.image;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.EXTBgra;
import org.lwjgl.opengl.GL11;

import net.aegistudio.transparent.Constant;

public enum EnumPixelFormat implements Constant{
	BYTE3_BGR(EXTBgra.GL_BGR_EXT, GL11.GL_UNSIGNED_BYTE, 3) {
		@Override
		public void convert(ByteBuffer targetBuffer, double red, double green, double blue, double alpha) {
			targetBuffer.put((byte)((1 << 8) * blue));
			targetBuffer.put((byte)((1 << 8) * green));
			targetBuffer.put((byte)((1 << 8) * red));
		}
	},
	BYTE3_RGB(GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, 3) {
		@Override
		public void convert(ByteBuffer targetBuffer, double red, double green, double blue, double alpha) {
			targetBuffer.put((byte)((1 << 8) * red));
			targetBuffer.put((byte)((1 << 8) * green));
			targetBuffer.put((byte)((1 << 8) * blue));
		}
	};
	
	public final int glPixelFormatId;
	public final int glPixelDataId;
	public final int sampleSize;
	
	private EnumPixelFormat(int glPixelFormatId, int glPixelDataId, int sampleSize) {
		this.glPixelFormatId = glPixelFormatId;
		this.glPixelDataId = glPixelDataId;
		this.sampleSize = sampleSize;
	}
	
	@Override
	public int getValue() {
		return glPixelFormatId;
	}
	
	public int getPixelFormatTypeValue() {
		return this.glPixelFormatId;
	}
	
	public int getPixelFormatDataValue() {
		return this.glPixelDataId;
	}
	
	public int getSampleSize() {
		return this.sampleSize;
	}
	
	public abstract void convert(ByteBuffer targetBuffer, double red, double green, double blue, double alpha);
}
