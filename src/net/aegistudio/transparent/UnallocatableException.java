package net.aegistudio.transparent;

@SuppressWarnings("serial")
public class UnallocatableException extends RuntimeException{
	
	public final Object errorAllocatable;
	
	public UnallocatableException(Object errorAllocatable) {
		super("Error while asking for more naming.");
		this.errorAllocatable = errorAllocatable;
	}
}
