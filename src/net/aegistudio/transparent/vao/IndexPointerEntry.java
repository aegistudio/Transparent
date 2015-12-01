package net.aegistudio.transparent.vao;

import net.aegistudio.transparent.vbo.VertexBufferObject;

public class IndexPointerEntry extends ArrayPointerEntry{
	public IndexPointerEntry(VertexBufferObject vbo) {
		super(new ArrayPointer() {
			@Override
			public void enable() {
				
			}

			@Override
			public void arrayPointer(int size, int type, int stride, long offset) {
				
			}

			@Override
			public void disable() {
				
			}

			@Override
			public int getDefaultSize() {
				return 1;
			}

			@Override
			public void draw() {
				vbo.bind();
			}	
		}, vbo);
	}
}
