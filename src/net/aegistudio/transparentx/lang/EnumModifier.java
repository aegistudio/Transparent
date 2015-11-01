package net.aegistudio.transparentx.lang;

/**
 * Only global identifiers will be appear here.
 * @author aegistudio
 */

public enum EnumModifier {
	/** GL BUILTINS **/
	BUILTIN_VARIABLE(true, true, false),
	BUILTIN_FUNCTION(true, false, true),
	
	/** GLOBAL VARIABLES **/
	ATTRIBUTE(false, true, false),
	UNIFORM(false, true, false),
	VARYING(false, true, false),
	CONST(false, true, false),
	BUFFER(false, true, false),
	SHARED(false, true, false),
	
	/** PARAMETERS OR LOCAL VARIABLES **/
	IN(false, true, false),
	OUT(false, true, false),
	INOUT(false, true, false),
	NONE(false, true, false),
	
	/** FUNCTIONS **/
	FUNCTION(false, false, true),
	MAIN(false, false, true);
	
	public final boolean builtin;
	public final boolean variable;
	public final boolean function;
	
	private EnumModifier(boolean builtin, boolean variable, boolean function) {
		this.builtin = builtin;
		this.variable = variable;
		this.function = function;
	}
}
