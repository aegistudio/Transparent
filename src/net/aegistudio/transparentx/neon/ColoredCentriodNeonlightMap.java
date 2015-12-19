package net.aegistudio.transparentx.neon;

import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparentx.ShaderResource;

public class ColoredCentriodNeonlightMap extends CentriodNeonlightMap{
	private int texTarget;
	public ColoredCentriodNeonlightMap(int texTarget, int order) {
		super(texTarget, order);
		this.texTarget = texTarget;
	}

	ShaderResource colored_centriod_neonlight_fsh = new ShaderResource("colored_centriod_neonlight.fsh"){};
	@Override
	public String[] getRenderSource(EnumShaderType shaderType) {
		if(shaderType == EnumShaderType.FRAGMENT) return new String[] {colored_centriod_neonlight_fsh.getResource().
				replaceAll("%srcCoord", Integer.toString(texTarget))};
		return null;
	}
}
