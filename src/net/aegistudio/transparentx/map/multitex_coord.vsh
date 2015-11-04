void main() {
	gl_Position = ftransform();
	gl_FrontColor = gl_BackColor = gl_Color;
	gl_TexCoord[%srcCoord] = gl_MultiTexCoord%srcCoord;
}