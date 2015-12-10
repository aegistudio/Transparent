package net.aegistudio.transparent.font;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import net.aegistudio.transparent.model.Drawable;
import net.aegistudio.transparent.model.Entity;

/**
 * To display a string either in a scene or in the viewport.
 * A character map is needed to display the chars.
 * @author aegistudio
 */

public class String extends Entity {

	private StringKernel stringKernel;
	public String() {
		super(new StringKernel());
		this.stringKernel = (StringKernel) super.wrapped;
	}
	
	public void setString(java.lang.String displayString) {
		this.stringKernel.setString(displayString);
	}
	
	Font previousFont;
	public void setFont(Font font, float width, float height) {
		if(previousFont != font) {
			if(previousFont != null) previousFont.uninstall(this);
			if(font != null) {
				font.install(this);
			}
			this.previousFont = font;
		}
		
		this.stringKernel.setFont(font == null? null : font.getCharacterMap(), width, height);
	}
	
	public void setVector(float vectorX, float vectorY) {
		this.stringKernel.setVector(vectorX, vectorY);
	}
}

class StringKernel implements Drawable {
	/**
	 * The string to display.
	 * @param displayString
	 */
	char[] displayString;
	public void setString(java.lang.String displayString) {
		if(displayString == null) this.displayString = null;
		else this.displayString = displayString.toCharArray();
	}
	
	/**
	 * The font to use.
	 * @param charMap
	 */
	CharacterMap charMap; float width, height;
	public void setFont(CharacterMap font, float width, float height) {
		this.charMap = font;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * When a char is drew, where will next char be.
	 * @param vectorX
	 * @param vectorY
	 */
	FloatBuffer vectorBuffer = BufferUtils.createFloatBuffer(16); {
		this.setVector(1.0f, 0.0f);
	}
	public void setVector(float vectorX, float vectorY) {
		vectorBuffer.put(new float[] {
			1,	0,	0,	0,
			0,	1,	0,	0,	
			0,	0,	1,	0,
			vectorX,	vectorY,	0,	1
		});
		vectorBuffer.flip();
	}

	
	@Override
	public void create() throws Exception {
		
	}

	@Override
	public void render() throws Exception {
		if(this.displayString == null) return;
		if(this.charMap == null) return;
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		for(char c : this.displayString) {
			this.charMap.begin(c);
			GL11.glBegin(GL11.GL_QUADS);
				this.charMap.setLeftDownCorner(); 
				GL11.glVertex2d(0, 0);
				
				this.charMap.setRightDownCorner(); 
				GL11.glVertex2d(this.width, 0);
				
				this.charMap.setRightUpCorner(); 
				GL11.glVertex2d(this.width, this.height);
				
				this.charMap.setLeftUpCorner(); 
				GL11.glVertex2d(0, this.height);
			GL11.glEnd();
			this.charMap.end();
			
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glMultMatrix(this.vectorBuffer);
		}
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();
	}

	@Override
	public void destroy() throws Exception {
		
	}
}
