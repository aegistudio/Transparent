package net.aegistudio.transparentx.map;

import net.aegistudio.transparent.shader.EnumShaderData;
import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparent.shader.VertexAttribute;
import net.aegistudio.transparentx.ShaderEffect;
import net.aegistudio.transparentx.ShaderEffectClass;
import net.aegistudio.transparentx.ShaderResource;
import net.aegistudio.transparentx.ShaderVertexAttribute;

public abstract class VertexAttributeCoordinating implements ShaderEffect {
	private final int textureTarget;
	private final ShaderResource vertattribute_coord_vsh = new ShaderResource("vertattribute_coord.vsh"){};
	
	public VertexAttributeCoordinating(int textureTarget) {
		this.textureTarget = textureTarget;
	}
	
	@Override
	public ShaderEffectClass getShaderEffectClass() {
		return new TextureCoordinating(textureTarget);
	}

	@Override
	public String[] getRenderSource(EnumShaderType shaderType) {
		if(shaderType == EnumShaderType.VERTEX)
			return new String[] {vertattribute_coord_vsh.getResource()
					.replaceAll("%srcCoord", Integer.toString(textureTarget))};
		else return null;
	}

	@Override
	public void setParameters() {	}
	
	public VertexAttribute getAttribute() {
		return new ShaderVertexAttribute(this, EnumShaderData.DVEC4, "texCoord");
	}
}
