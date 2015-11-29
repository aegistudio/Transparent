package net.aegistudio.transparent.image;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.nio.ByteBuffer;
import java.util.TreeMap;

public enum EnumImageFormat {
	BYTE3_BGR(BufferedImage.TYPE_3BYTE_BGR) {
		@Override
		public void extract(EnumPixelFormat format, Translator translator, Raster raster, int x, int y, ByteBuffer targetBuffer) {
			translator.translate(raster.getSampleDouble(x, y, 0) / 256.0,
					raster.getSampleDouble(x, y, 1) / 256.0,
					raster.getSampleDouble(x, y, 2) / 256.0, 1.0);
			format.convert(targetBuffer, translator.getRedComponent(),
					translator.getGreenComponent(), translator.getBlueComponent(),
					translator.getAlphaComponent());
		}
	};
	
	private static final TreeMap<Integer, EnumImageFormat> imageFormatMap
		= new TreeMap<Integer, EnumImageFormat>();
		
	public final int javaImageFormatId;
	
	private EnumImageFormat(int javaImageFormatId) {
		this.javaImageFormatId = javaImageFormatId;
	}
	
	static {
		for(EnumImageFormat format : values())
			imageFormatMap.put(format.javaImageFormatId, format);
	}
	
	public static EnumImageFormat getImageFormat(int formatId) {
		return imageFormatMap.get(formatId);
	}
	
	public abstract void extract(EnumPixelFormat format, Translator translator, Raster raster, int x, int y, ByteBuffer byteBuffer);
}
