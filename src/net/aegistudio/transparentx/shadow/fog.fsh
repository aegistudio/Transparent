varying float distance;
uniform float x0;
uniform float x1;
uniform float x2;
uniform float threshold;

void main() {
	float attenuation = x0 / (x0 + x1 * distance + x2 * distance * distance);
	if(attenuation < threshold) discard;
	else gl_FragColor *= attenuation;
}