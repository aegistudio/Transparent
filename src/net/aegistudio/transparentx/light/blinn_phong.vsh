/**
 * Per-pixel Lighting (Vertex Shader)
 * This lighting shader treat every pixels
 * on rendered fragment as a shading unit.
 * There're some sharing variables here, for
 * the convenience of shading extension.
 */

varying vec4 _viewVector_interpolate;
varying vec3 _normal_interpolate;
varying mat3 _normal_matrix;
varying mat4 _modelview_matrix;

void main() {
	// Normal positional and color processing.
	gl_Position = ftransform();
	gl_FrontColor = gl_BackColor = gl_Color;

	// View point at (0,0,0,1), so view vector should
	// Be the vector in coordinate.
	_viewVector_interpolate = gl_ModelViewMatrix * gl_Vertex;
	_modelview_matrix = gl_ModelViewMatrix;
	
	// Transform normal vector for calculation.
	_normal_interpolate = gl_NormalMatrix * gl_Normal;
	_normal_matrix = gl_NormalMatrix;
}
