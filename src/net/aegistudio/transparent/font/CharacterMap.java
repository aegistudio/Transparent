package net.aegistudio.transparent.font;

/**
 * A character describes how to map the font.
 * 
 * @author aegistudio
 */

public interface CharacterMap {
	public void begin(char input);
	
	public void setLeftUpCorner();
	
	public void setRightUpCorner();
	
	public void setLeftDownCorner();
	
	public void setRightDownCorner();
	
	public void end();
}
