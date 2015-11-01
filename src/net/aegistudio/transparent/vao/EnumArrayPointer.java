package net.aegistudio.transparent.vao;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import net.aegistudio.transparent.Constant;

public enum EnumArrayPointer implements ArrayPointer, Constant{
	VERTEX(GL11.GL_VERTEX_ARRAY, 3) {
		public void arrayPointer(int size, int type, int stride, long offset) {
			GL11.glVertexPointer(size, type, stride, offset);
		}
	},
	
	TEXTURE(GL11.GL_TEXTURE_COORD_ARRAY, 2) {
		public void arrayPointer(int size, int type, int stride, long offset) {
			GL11.glTexCoordPointer(size, type, stride, offset);
		}
	},
	
	COLOR(GL11.GL_COLOR_ARRAY, 4) {
		public void arrayPointer(int size, int type, int stride, long offset) {
			GL11.glColorPointer(size, type, stride, offset);
		}
	},
	
	SECONDARY_COLOR(GL14.GL_SECONDARY_COLOR_ARRAY, 4) {
		public void arrayPointer(int size, int type, int stride, long offset) {
			GL14.glSecondaryColorPointer(size, type, stride, offset);
		}
	},
	
	FOG(GL14.GL_FOG_COORDINATE_ARRAY, 1) {
		public void arrayPointer(int size, int type, int stride, long offset) {
			GL14.glFogCoordPointer(type, stride, offset);
		}
	},
	
	NORMAL(GL11.GL_NORMAL_ARRAY, 3) {
		public void arrayPointer(int size, int type, int stride, long offset) {
			GL11.glNormalPointer(type, stride, offset);
		}
	},
	
	EDGE_FLAG(GL11.GL_EDGE_FLAG_ARRAY, 1) {
		public void arrayPointer(int size, int type, int stride, long offset) {
			GL11.glEdgeFlagPointer(stride, offset);
		}
	};
	
	private final int arrayPointerId;
	
	private final int defaultSize;
	
	private EnumArrayPointer(int stateName, int defaultSize) {
		this.arrayPointerId = stateName;
		this.defaultSize = defaultSize;
	}
	
	public abstract void arrayPointer(int size, int type, int stride, long offset);
	
	public void enable() {
		GL11.glEnableClientState(arrayPointerId);
	}
	
	public void disable() {
		GL11.glDisableClientState(arrayPointerId);
	}
	
	public int getDefaultSize() {
		return this.defaultSize;
	}
	
	public int getValue() {
		return this.arrayPointerId;
	}
	
	public void draw() {
		
	}
}
