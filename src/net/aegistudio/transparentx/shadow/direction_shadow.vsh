uniform mat4 lightTransform;
uniform mat4 coordTransform;
uniform mat4 projTransform;

varying vec2 accessVector;
varying float accessDepth;

uniform bool directionalLight;

void main() {
	if(directionalLight) {
		vec4 posVector = gl_Vertex;
		posVector.x = -posVector.x;
		posVector.y = -posVector.y;
	
		vec4 vectorTemp = lightTransform * projTransform * gl_ModelViewProjectionMatrix * posVector;
		vectorTemp /= vectorTemp.w;
	
		vec4 vectorTemp2 = coordTransform * vectorTemp;
		
		accessVector = vectorTemp2.xy / vectorTemp2.w;
		accessDepth = (1.0 - vectorTemp.z) / 2.0;
	}
}