package net.aegistudio.transparent.font;

import org.lwjgl.opengl.GL11;

public class ContentMap implements CharacterMap {
	
	private final java.lang.String content;
	private final double bias;
	public ContentMap(java.lang.String content) {
		this.content = content;
		this.bias = 1.0 / content.length();
	}
	
	double current;
	@Override
	public void begin(char input) {
		this.current = content.indexOf(input) * bias;
	}

	@Override
	public void setLeftUpCorner() {
		GL11.glTexCoord2d(current + 0, 1.0);
	}

	@Override
	public void setRightUpCorner() {
		GL11.glTexCoord2d(current + bias, 1.0);		
	}

	@Override
	public void setLeftDownCorner() {
		GL11.glTexCoord2d(current + 0, 0.0);
	}

	@Override
	public void setRightDownCorner() {
		GL11.glTexCoord2d(current + bias, 0.0);
	}

	@Override
	public void end() {
		
	}
}
