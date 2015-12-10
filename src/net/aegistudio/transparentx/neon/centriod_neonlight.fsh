uniform sampler2D texTarget;
uniform vec4 componentFactor;
uniform vec2 bias;

/** Centriod **/
uniform float radium;
uniform float step;
uniform float significance;
uniform int direction;

void main() {
	vec4 color = gl_FragColor;
	if(dot(texture2D(texTarget, gl_TexCoord[%srcCoord].st), componentFactor) > 0.5) {
		gl_FragColor = abs(color + vec4(significance));
	}
	else {
		int i, j;	float illuminance = 0.0;
		for(i = -direction; i <= direction; i ++)	for(j = -direction; j <= direction; j ++) {
			if(i == 0 && j == 0) continue;
			vec2 direction = normalize(vec2(float(i), float(j)));
			float len;
			for(len = 0.0; len <= radium; len += step) {
				illuminance += dot(texture2D(texTarget, 
					gl_TexCoord[%srcCoord].st + direction * len), 
					componentFactor) * step;
			}
		}
		
		gl_FragColor = abs(illuminance * (color + vec4(significance)) / (float(i * j) * radium));
	}
}