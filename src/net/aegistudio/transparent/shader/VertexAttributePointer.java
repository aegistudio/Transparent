package net.aegistudio.transparent.shader;

import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;

import net.aegistudio.transparent.FeatureUnsupportedException;
import net.aegistudio.transparent.vao.ArrayPointer;

public class VertexAttributePointer implements ArrayPointer {

	private final VertexAttribute vertexAttribute;
	private final boolean normalized;
	private final int index;
	
	public VertexAttributePointer(VertexAttribute vertexAttribute, boolean normalized, int index) {
		this.vertexAttribute = vertexAttribute;
		this.normalized = normalized;
		this.index = index;
	}
	
	@Override
	public void enable() {
		if(index >= GL11.glGetInteger(ARBVertexShader.GL_MAX_VERTEX_ATTRIBS_ARB))
			throw new FeatureUnsupportedException(String.format("vertex attribute #%d", index));
		ARBVertexShader.glEnableVertexAttribArrayARB(index);
	}

	@Override
	public void arrayPointer(int size, int type, int stride, long offset) {
		ARBVertexShader.glVertexAttribPointerARB(index,
				size, type, normalized, stride, 0);
	}

	@Override
	public void disable() {
		ARBVertexShader.glDisableVertexAttribArrayARB(index);
	}

	@Override
	public int getDefaultSize() {
		return this.vertexAttribute.getAttributeType()
				.getParameterLength();
	}

	@Override
	public void draw() {
		vertexAttribute.bindVertexArrayPointer(index);
	}
}
