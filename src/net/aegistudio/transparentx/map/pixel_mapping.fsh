uniform sampler2D texTarget;
uniform float distortionFactor;

void main() {
	vec2 texCoord = gl_TexCoord[%srcCoord].st;
	texCoord.s = round(texCoord.s / distortionFactor - 0.5) * distortionFactor;
	texCoord.t = round(texCoord.t / distortionFactor - 0.5) * distortionFactor;
	
	gl_FragColor = texture2D(texTarget, texCoord);
}