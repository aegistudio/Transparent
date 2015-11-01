package net.aegistudio.transparent.image;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class RasterImage implements Image {
	private final ByteBuffer targetBuffer;
	private final EnumPixelFormat pixelFormat;
	private final int width, height;
	
	public RasterImage(BufferedImage image, EnumPixelFormat pixelFormat) {
		this(image.getRaster(), EnumImageFormat.getImageFormat(image.getType()), pixelFormat);
	}
	
	public RasterImage(Raster raster, EnumImageFormat imageFormat,
			EnumPixelFormat pixelFormat) {
		
		targetBuffer = BufferUtils.createByteBuffer(
				raster.getWidth() *raster.getHeight() * pixelFormat.getSampleSize());
		for(int i = raster.getHeight() - 1; i >= 0; i --)
			for(int j = 0; j < raster.getWidth(); j ++)
				imageFormat.extract(pixelFormat, raster, j, i, targetBuffer);
		targetBuffer.flip();
		
		this.pixelFormat = pixelFormat;
		
		this.width = raster.getWidth();
		this.height = raster.getHeight();
	}
	
	public void textureImage2D(int texTarget, int level, EnumPixelFormat internalFormat, int border) {
		GL11.glTexImage2D(texTarget, level, internalFormat.getPixelFormatTypeValue(), width, height,
				border, pixelFormat.getPixelFormatTypeValue(), pixelFormat.getPixelFormatDataValue(), targetBuffer);
	}

	@Override
	public EnumPixelFormat getPixelFormat() {
		return pixelFormat;
	}
}