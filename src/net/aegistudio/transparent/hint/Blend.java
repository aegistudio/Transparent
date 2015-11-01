package net.aegistudio.transparent.hint;

import java.util.Stack;
import org.lwjgl.opengl.GL11;
import net.aegistudio.transparent.model.Effect;

public class Blend implements Effect{
	protected EnumBlendMethod blendMethod = EnumBlendMethod.DECALATE;
	
	public void setBlendMethod(EnumBlendMethod blendMethod) {
		this.blendMethod = blendMethod;
	}
	
	@Override
	public void create() throws Exception {	}

	@Override
	public void use() throws Exception {
		GL11.glEnable(GL11.GL_BLEND);
		this.blendMethod.set();
	}
	
	@Override
	public void recover() throws Exception {
		if(blendStack.isEmpty()) GL11.glDisable(GL11.GL_BLEND);
		else blendStack.pop().blendMethod.set();
	}

	@Override
	public void destroy() throws Exception {	}

	protected Stack<Blend> blendStack = new Stack<Blend>();
}
