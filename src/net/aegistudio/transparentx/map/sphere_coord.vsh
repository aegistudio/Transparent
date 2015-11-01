void main() {
	gl_Position = ftransform();
	gl_TexCoord[%dstCoord] = gl_Vertex;
}