package net.aegistudio.transparentx.combine;

public class NomodifyRedundant implements Combine{
	private final int count;
	public NomodifyRedundant(int count) {
		this.count = count;
	}
	
	@Override
	public void combine(String type, String target, String prefix) {
		
	}

	@Override
	public String getPreprocessCode() {
		return "";
	}

	@Override
	public String getPostprocessCode() {
		return "";
	}

	@Override
	public int getReplacement() {
		return count;
	}
}
