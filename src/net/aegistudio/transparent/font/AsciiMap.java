package net.aegistudio.transparent.font;

import org.lwjgl.opengl.GL11;

/**
 * Receive ascii code and transfer it into character.
 * @author aegistudio
 */

public class AsciiMap implements CharacterMap {
	static final float offset = 1.0f / 16;
	
	float br, bc;
	@Override
	public void begin(char input) {
		int row = 15 - (input >>> 0) & 0x000f;
		int column = (input >>> 4) & 0x000f;
		
		br = column * offset;
		bc = row * offset;
	}

	@Override
	public void setLeftUpCorner() {
		GL11.glTexCoord2d(br + 0, bc + offset);
	}

	@Override
	public void setRightUpCorner() {
		GL11.glTexCoord2d(br + offset, bc + offset);
	}

	@Override
	public void setLeftDownCorner() {
		GL11.glTexCoord2d(br + 0, bc + 0);
	}

	@Override
	public void setRightDownCorner() {
		GL11.glTexCoord2d(br + offset, bc + 0);
	}

	@Override
	public void end() {
		
	}
}
