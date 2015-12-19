varying vec2 unifiedCoord;

void main() {
	vec4 position = ftransform();
	unifiedCoord = position.xy / position.w;
	unifiedCoord = unifiedCoord / 2.0 + vec2(0.5, 0.5);
}