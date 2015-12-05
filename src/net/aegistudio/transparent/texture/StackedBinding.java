package net.aegistudio.transparent.texture;

import net.aegistudio.transparent.UnallocatableException;
import net.aegistudio.transparent.UninitializedException;

public class StackedBinding extends TextureBinding {
	private int targetBinding = -1;
	
	public StackedBinding(Texture targetTexture, boolean scoped) {
		super(targetTexture, scoped);
	}

	@Override
	public void use() {
		if(targetBinding == -1) {
			for(int i = maxMultiTextureUnit - 1; i >= 0; i --) 
				if(currentMultiTexture[i] == null) {targetBinding = i; break;}
			if(targetBinding == -1) throw new UnallocatableException(this);
		}
		else throw new UninitializedException(this);
		super.bindMultitexture(targetBinding);
	}

	@Override
	public void recover() {
		super.unbindMultitexture(targetBinding);
		targetBinding = -1;
	}
	
	@Override
	public int getCurrentBinding() {
		return targetBinding;
	}
}
