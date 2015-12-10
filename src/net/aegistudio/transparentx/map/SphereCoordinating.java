package net.aegistudio.transparentx.map;

import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparentx.ShaderEffect;
import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.ShaderResource;

public abstract class SphereCoordinating implements ShaderEffect {
	private final int textureTarget;
	private final ShaderResource sphere_coord_vsh = new ShaderResource("sphere_coord.vsh"){};
	
	public SphereCoordinating(int textureTarget) {
		this.textureTarget = textureTarget;
	}
	
	@Override
	public ShaderEffectClass getShaderEffectClass() {
		return new TextureCoordinating(textureTarget);
	}
	
	@Override
	public String[] getRenderSource(EnumShaderType shaderType) {
		if(shaderType == EnumShaderType.VERTEX)
			return new String[] {sphere_coord_vsh.getResource()
					.replaceAll("%srcCoord", Integer.toString(textureTarget))};
		else return null;
	}
	
	public void setParameters() {
		
	}
}
