package net.aegistudio.transparent;

@SuppressWarnings("serial")
public class UninitializedException extends RuntimeException {
	private final Object obj;
	
	public UninitializedException(Object obj){
		super(String.format("Instance %s#%x remains uninitialized before using it!",
				obj.getClass(), obj.hashCode()));
		this.obj = obj;
	}
	
	public Object getUninitializedObject() {
		return this.obj;
	}
}
