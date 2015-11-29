package net.aegistudio.transparentx.lang;

public class Symbol {
	public final String name;
	public final String type;
	public final EnumModifier modifier;
	public final String scope;
	
	public final String arrayLength;
	
	public Symbol(String symbolName, String symbolType,
			EnumModifier symbolModifier, String scope, String arrayLength) {
		this.name = symbolName;
		this.type = symbolType;
		this.modifier = symbolModifier;
		this.scope = scope;
		this.arrayLength = arrayLength;
	}
	
	public String toDefinition(String name) {
		StringBuilder builder;
		if(modifier == EnumModifier.NONE) builder = new StringBuilder();
		else {
			builder = new StringBuilder(modifier.toString().toLowerCase());
			builder.append(' ');
		}
		builder.append(type);
		builder.append(' ');
		builder.append(name);
		
		if(arrayLength != null){
			builder.append('[');
			builder.append(arrayLength);
			builder.append(']');
		}
		
		return new String(builder);
	}
}
