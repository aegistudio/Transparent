varying vec3 normalVector;
varying vec4 vertexVector;

void main() {
	vertexVector = ftransform();
	vertexVector /= vertexVector.w;
	vertexVector.xy = vec2(0.5, 0.5) + vertexVector.xy / 2.0;
	
	normalVector = gl_NormalMatrix * gl_Normal;
	normalVector.xy *= -1.0;
}