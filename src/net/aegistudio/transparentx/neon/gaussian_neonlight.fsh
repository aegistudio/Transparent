uniform sampler2D texTarget;
uniform vec4 componentFactor;
uniform vec2 bias;

/** Gaussian Bases **/
uniform mat3 gaussianBase;
uniform float significance;

void main() {
	vec4 color = gl_FragColor;
	if(dot(texture2D(texTarget, gl_TexCoord[%srcCoord].st), componentFactor) > 0.5) {
		gl_FragColor = abs(color + vec4(significance));
	}
	else {
		int i, j;	float illuminance = 0.0;
		for(i = 0; i < 3; i ++)	for(j = 0; j < 3; j ++)
			illuminance += gaussianBase[i][j] * dot(texture2D(texTarget, 
				gl_TexCoord[%srcCoord].st + vec2(float(i - 1) * bias.x, float(j - 1) * bias.y)),
					componentFactor);
		
		gl_FragColor = illuminance * abs(color + vec4(significance));
	}
}