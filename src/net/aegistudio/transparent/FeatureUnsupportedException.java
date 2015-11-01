package net.aegistudio.transparent;

@SuppressWarnings("serial")
public class FeatureUnsupportedException extends RuntimeException{
	
	public final String featureName;
	
	public FeatureUnsupportedException(String featureName) {
		super(String.format("we're sorry to tell you that the feature %s is not available.", featureName));
		this.featureName = featureName;
	}
	
	public String getFeatureName() {
		return this.featureName;
	}
}
