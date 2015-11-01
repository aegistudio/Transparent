package net.aegistudio.transparentx.combine;

/**
 * Using this will force storage of modified variable.
 * If this is a interface variable and will not be used
 * any more, please use ModifyReplaceStreamed.
 * @author aegistudio
 */

@Deprecated
public class NomodifyStreamed extends Streamed{
	public NomodifyStreamed(int modifications) {
		super(new NomodifyOriginal(), modifications);
	}
	
	public NomodifyStreamed() {
		this(1);
	}
}
