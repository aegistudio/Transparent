uniform sampler2D texTarget;
vec4 _viewVector;
vec3 _normal; 
uniform vec4 modulator;
uniform vec2 dx, dy;
varying mat4 _modelview_matrix;
varying mat3 _normal_matrix;
uniform float heightOffset;

void main() {
	gl_FragColor = gl_Color;
	float height = dot(modulator, texture2D(texTarget, gl_TexCoord[%srcCoord].st)) + heightOffset;
	_viewVector = _viewVector + height * _modelview_matrix * vec4(_normal, 1.0);
	float hdx = dot(modulator, texture2D(texTarget, gl_TexCoord[%srcCoord].st + dx))
		 - dot(modulator, texture2D(texTarget, gl_TexCoord[%srcCoord].st - dx));
	float hdy = dot(modulator, texture2D(texTarget, gl_TexCoord[%srcCoord].st + dy))
		 - dot(modulator, texture2D(texTarget, gl_TexCoord[%srcCoord].st - dy));
	_normal = normalize(cross(vec3(dx, hdx), vec3(dy, hdy)));
}