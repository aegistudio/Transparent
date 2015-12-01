package net.aegistudio.transparentx.map;

import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparentx.ShaderEffect;
import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.ShaderResource;

public abstract class DirectCoordinating implements ShaderEffect {
	private final int textureTarget;
	private final ShaderResource multitex_coord_vsh = new ShaderResource("multitex_coord.vsh"){};
	
	public DirectCoordinating(int textureTarget) {
		this.textureTarget = textureTarget;
	}
	
	@Override
	public ShaderEffectClass getShaderEffectClass() {
		return new TextureCoordinating(textureTarget);
	}
	
	@Override
	public String[] getRenderSource(EnumShaderType shaderType) {
		if(shaderType == EnumShaderType.VERTEX)
			return new String[] {multitex_coord_vsh.getResource()
					.replaceAll("%srcCoord", Integer.toString(textureTarget))};
		else return null;
	}
	
	public void setParameters() {
		
	}
}
