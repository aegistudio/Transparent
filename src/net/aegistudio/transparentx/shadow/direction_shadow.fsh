varying vec2 accessVector;
varying float accessDepth;
uniform sampler2D shadowMap;
uniform float regulation;
float _shadow[gl_MaxLights];

uniform bool directionalLight;

void main() {
	if(directionalLight) {
		float depth = texture2D(shadowMap, accessVector.xy).z;
		if(accessDepth < (1.0 - regulation) * (1.0 - depth)) 
			_shadow[%targetLight] = 0.0;
	}
}