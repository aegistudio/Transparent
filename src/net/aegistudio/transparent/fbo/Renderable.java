package net.aegistudio.transparent.fbo;

public interface Renderable {
	public boolean attach(FrameBufferAttachment attachment);
	
	public boolean detach(FrameBufferAttachment attachment);
}
