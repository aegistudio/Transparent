package net.aegistudio.transparentx.lang;

import java.util.TreeMap;

public enum EnumKeyword {
	MODIFIER("uniform", "attribute", "varying", "const", "in", "out", "inout", "buffer", "shared"),
	BUILTIN_VARIABLE(SharingVariable.getBuiltins()),
	BUILTIN_FUNCTION("ftransform", "sin", "cos", "tan", "exp"),
	TYPE(	// BASICS
			"void", "int", "float", "double", "bool",
			// VECTOS
			"vec2", "vec3", "vec4", 
			"ivec2", "ivec3", "ivec4",
			"bvec2", "bvec3", "bvec4", 
			// MATRICES
			"mat2", "mat3", "mat4", 
			"mat2x2", "mat2x3", "mat2x4",
			"mat3x2", "mat3x3", "mat3x4",
			"mat4x2", "mat4x3", "mat4x4",
			// TEXTURES
			"sampler1D", "sampler2D", "sampler3D", "samplerCube",
			"sampler1DShadow","sampler2DShadow","samplerCubeShadow"
			),
	CONTROL("if", "else", "while", "do", "for", "switch", "case", "default", "discard", "break", "continue");
	
	private static final TreeMap<String, EnumKeyword> keywords
		= new TreeMap<String, EnumKeyword>();
	
	private final String[] acceptedKeywords;
	
	private EnumKeyword(String... acceptedKeywords) {
		this.acceptedKeywords = acceptedKeywords;
	}
	
	static  {
		for(EnumKeyword kw : values())
			for(String str : kw.acceptedKeywords)
				keywords.put(str, kw);
	}
	
	public static EnumKeyword getKeyword(String input) {
		return keywords.get(input);
	}
}
