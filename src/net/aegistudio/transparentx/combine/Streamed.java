package net.aegistudio.transparentx.combine;

public class Streamed implements Combine{
	private final int modifications;
	
	private final Combine original;
	
	public Streamed(Combine original, int modifications) {
		this.modifications = modifications;
		this.original = original;
	}
	
	public Streamed(Combine original) {
		this(original, 1);
	}

	@Override
	public int getReplacement() {
		return modifications;
	}

	@Override
	public void combine(String type, String target, String prefix) {
		original.combine(type, target, prefix);
	}

	@Override
	public String getPreprocessCode() {
		return original.getPreprocessCode();
	}

	@Override
	public String getPostprocessCode() {
		return original.getPostprocessCode();
	}
}
