package net.aegistudio.transparent.image;

public interface Image {
	public EnumPixelFormat getPixelFormat();
	
	public void textureImage2D(int texTarget, int level, EnumPixelFormat internalFormat, int border);
}
