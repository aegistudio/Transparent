package net.aegistudio.transparent.texture;

public class ConstantBinding extends TextureBinding {

	private int targetBinding;
	
	public ConstantBinding(Texture targetTexture, int targetBinding, boolean scoped) {
		super(targetTexture, scoped);
		this.targetBinding = targetBinding;
	}
	
	public ConstantBinding(Texture targetTexture, int targetBinding) {
		this(targetTexture, targetBinding, false);
	}
	
	public ConstantBinding(Texture targetTexture) {
		this(targetTexture, 0);
	}

	@Override
	public void use() {
		this.bindMultitexture(targetBinding);
	}

	@Override
	public void recover() {
		this.unbindMultitexture(targetBinding);
	}

	@Override
	public int getCurrentBinding() {
		return targetBinding;
	}

}
