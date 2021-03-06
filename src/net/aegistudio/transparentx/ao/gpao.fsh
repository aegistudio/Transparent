varying vec3 normalVector;
varying vec4 vertexVector;

uniform sampler2D depthMap;
uniform float windowSize;

const int detailLevel = %detailLevel;
void main() {
	float step = windowSize / float(detailLevel);
	float depth = (1.0 + vertexVector.z) / 2.0;
	
	if(depth - texture2D(depthMap, vertexVector.xy).z < -0.1) return;
	
	float accumOcclusion = 0.0;
	float acceptedFragments = 0.0;
	int i, j;
	
	vec2 pivotOffset = vec2(0.0, 0.0);
	float initStep = float(-detailLevel) * step;
	
	pivotOffset.x = initStep;
	for(i = -detailLevel; i <= detailLevel; i ++) {
		pivotOffset.x += step;
		pivotOffset.y = initStep;
		for(j = -detailLevel; j <= detailLevel; j ++) {
			pivotOffset.y += step;
			vec2 targetVector = vertexVector.xy + pivotOffset;
			if(targetVector.x > 1.0 || targetVector.x < 0.0 
				|| targetVector.y > 1.0 || targetVector.y < 0.0) continue;
			
			acceptedFragments += 1.0;
			
			float lend = dot(normalize(normalVector), normalize(vec3(pivotOffset,
				depth - texture2D(depthMap, targetVector).z)));
			if(lend > 0.0) accumOcclusion += lend;
		}
	}
	
	if(acceptedFragments > 0.0)
		gl_FragColor *= (1.0 - (accumOcclusion / acceptedFragments));
}