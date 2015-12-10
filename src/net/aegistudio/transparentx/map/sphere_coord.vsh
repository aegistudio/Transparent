void main() {
	vec4 normalizedVertex = normalize(gl_Vertex);
			// norm = (cos phi * cos theta, sin phi * sin theta, sin phi).
	
	float degreeLongitude = degrees(acos(normalizedVertex.y / 
		sqrt(1.0 - normalizedVertex.z * normalizedVertex.z))) / 360.0;
	float u = normalizedVertex.x >= 0.0? degreeLongitude : degreeLongitude + 0.5;
	float v = degrees(asin(normalizedVertex.z)) / 90.0;
	gl_TexCoord[%srcCoord] = vec4(u, v, 0, 0);
}