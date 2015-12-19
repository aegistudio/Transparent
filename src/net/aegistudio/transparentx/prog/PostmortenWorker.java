package net.aegistudio.transparentx.prog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.aegistudio.transparent.shader.ShaderObject;
import net.aegistudio.transparentx.lang.Symbol;

public abstract class PostmortenWorker implements SubprogramWorker{
	@Override
	public void compile() throws Exception {
		
	}

	List<ShaderObject> objects = new ArrayList<ShaderObject>();
	@Override
	public List<ShaderObject> object() {
		return objects;
	}

	@Override
	public abstract Map<String, Symbol> globalVariables();

	@Override
	public abstract String callbackBlock();

	@Override
	public abstract Set<String> normalProcessVariable();

}
