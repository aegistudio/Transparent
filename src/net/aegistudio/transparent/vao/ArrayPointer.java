package net.aegistudio.transparent.vao;

public interface ArrayPointer {
	public void enable();
	
	public void arrayPointer(int size, int type, int stride, long offset);
	
	public void disable();
	
	public int getDefaultSize();
	
	public void draw();
}
