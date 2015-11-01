package net.aegistudio.transparentx.combine;

public class ModifyReplaceStreamed extends Streamed {
	public ModifyReplaceStreamed(int modifications) {
		super(new ModifyReplaceOriginal(), modifications);
	}
	
	public ModifyReplaceStreamed() {
		this(1);
	}
}
