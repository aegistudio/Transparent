package net.aegistudio.transparentx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public abstract class ShaderResource {
	private final String resourceName;
	
	protected ShaderResource(String resourceName) {
		this.resourceName = resourceName;
	}
	
	public String getResource() {
		try {
			File source = new File(this.getClass().getResource(resourceName).toURI());
			BufferedReader br = new BufferedReader(new FileReader(source));
			String currentLine = null; StringBuilder builder = new StringBuilder();
			while((currentLine = br.readLine()) != null) {
				builder.append(currentLine);
				builder.append('\n');
			}
			br.close();
			return new String(builder);
		}
		catch(Exception e) {
			return null;
		}
	}
}
