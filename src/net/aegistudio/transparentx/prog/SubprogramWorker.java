package net.aegistudio.transparentx.prog;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.aegistudio.transparent.shader.ShaderObject;
import net.aegistudio.transparentx.lang.Symbol;

/**
 * Subprogram worker helps working with the source code building of subprogram, the compilation
 * of the subprogram and the linking, attachment of subprogram.
 * 
 * @author aegistudio
 */
public interface SubprogramWorker {
	/** 
	 * Do preprocess to shader source code, and then compile (to make sure no syntax error in it).
	 * @throws Any exception reveals the failure of compilation, and ends up with fatal error
	 */
	public void compile() throws Exception;
	
	public List<ShaderObject> object();

	/**
	 * @return the global variables according to designated program.
	 */
	public Map<String, Symbol> globalVariables();
	
	/**
	 * @return the callback block in the main code, to run subprogram.
	 */
	public String callbackBlock();
	
	/**
	 * @return the shared variables needs to be preprocessed.
	 */
	public Set<String> normalProcessVariable();
}
