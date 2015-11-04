uniform sampler2D texTarget;

void main() {
	gl_FragColor = texture2D(texTarget, gl_TexCoord[%srcCoord].st);
}