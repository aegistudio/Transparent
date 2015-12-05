varying float distance;

void main() {
	distance = 1.0 - (gl_Position.z / gl_Position.w);
}