uniform sampler2D texTarget;
vec3 _normal;
varying mat3 _normal_matrix;

void main() {
	gl_FragColor = gl_Color;
	_normal = normalize(_normal_matrix * (2.0 * vec3(texture2D(texTarget,
		 gl_TexCoord[%srcCoord].st) - 1.0)));
}