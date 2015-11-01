attribute vec4 texCoord;

void main() {
	gl_Position = ftransform();
	gl_TexCoord[%dstCoord] = texCoord;
}