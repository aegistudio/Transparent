package net.aegistudio.transparentx.combine;

/**
 * Which means the result will replace the previous result.
 * 
 * @author aegistudio
 */

public class ModifyReplaceOriginal implements Combine {

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
		return 0;
	}

}
