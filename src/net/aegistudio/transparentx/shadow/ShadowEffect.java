package net.aegistudio.transparentx.shadow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import net.aegistudio.transparent.shader.EnumShaderType;
import net.aegistudio.transparentx.ShaderEffect;
import net.aegistudio.transparentx.ShaderEffectClass;

public class ShadowEffect implements ShaderEffect{
	public static void main(String[] arguments) throws Exception{
		File file = new File(ShadowEffect.class.getResource("shadow.vsh").toURI());
		if(file.exists()) {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str;
			while((str = br.readLine()) != null)System.out.println(str);
			br.close();
		}
	}

	@Override
	public ShaderEffectClass getShaderEffectClass() {
		return null;
	}

	@Override
	public String[] getRenderSource(EnumShaderType shaderType) {
		return null;
	}

	@Override
	public void setParameters() {
		
	}
}
