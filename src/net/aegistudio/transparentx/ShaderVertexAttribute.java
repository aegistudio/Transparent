package net.aegistudio.transparentx;

import net.aegistudio.transparent.shader.EnumShaderData;
import net.aegistudio.transparent.shader.VertexAttribute;

public class ShaderVertexAttribute extends VertexAttribute {
	public ShaderVertexAttribute(ShaderEffect effect,
			EnumShaderData uniformData, String uniformName) {
		super(uniformData, uniformName.charAt(0) == '_'? uniformName : String.format("%s_%s", 
				effect.getClass().getName().replaceAll("[\\.$]", "_"), uniformName));
	}
}
