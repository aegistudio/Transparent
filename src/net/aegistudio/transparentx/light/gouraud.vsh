/**
 * Per-vertex Lighting (Vertex Shader)
 * This lighting shader treats every vertex as a 
 * lighting unit. Calculate light intensity for it as
 * the vertex color, and interpolate it at fragment
 * shaders.
 */
 
void main() {
	// Normal positional processing.
	gl_Position = ftransform();

	// View point at (0,0,0,1), so view vector should
	// Be the vector in coordinate.
	vec4 viewVector = gl_ModelViewMatrix * gl_Vertex;
	vec3 eyeVector = vec3(viewVector) / viewVector.w;
	
	// Transform normal vector for calculation.
	vec3 normal = vec3(normalize(gl_NormalMatrix * gl_Normal));

	// Poll every lightings to calculate their light value.
	vec4 ambients = vec4(0.0, 0.0, 0.0, 0.0);
	vec4 diffuses = vec4(0.0, 0.0, 0.0, 0.0);
	vec4 speculars = vec4(0.0, 0.0, 0.0, 0.0);
	
	int current;
	int maxLights = gl_MaxLights;

	for(current = 0; current < maxLights; current ++) {
		// Calculate dot products.
		vec3 lightVector = vec3(gl_LightSource[current].position);
		float normal_light;
		float normal_halfVector;
		
		// Calculate factor for specular attenuation.
		float specularFactor = 0.0;
		
		if(gl_LightSource[current].position.w == 0.0) {
			// DirectionalLightSource
			normal_light = max(0.0, dot(normal, lightVector));
			normal_halfVector = max(0.0, dot(normal, vec3(gl_LightSource[current].halfVector)));
			if(normal_light > 0.0) specularFactor = pow(normal_halfVector, gl_FrontMaterial.shininess);

			ambients += gl_LightSource[current].ambient;
			diffuses += gl_LightSource[current].diffuse * normal_light;
			speculars += gl_LightSource[current].specular * specularFactor;
		}	
		else {
			// PointLightSource or SpotlightSource
			
			// Calculate lightVector minus viewVector, then the distance.
			vec3 light_view = lightVector - eyeVector;
			float lightDistance = length(light_view);
			light_view = normalize(light_view);

			// Calculate the specular attenuation.
			float attenuation = 1.0 / (gl_LightSource[current].constantAttenuation
				+ gl_LightSource[current].linearAttenuation * lightDistance
				+ gl_LightSource[current].quadraticAttenuation * lightDistance * lightDistance);

			// Recalculate half vector.
			vec3 halfVector = normalize(light_view + eyeVector);

			// Reculculate the dot products.
			normal_light = max(0.0, dot(normal, light_view));
			normal_halfVector = max(0.0, dot(normal, halfVector));

			// Calculate factor for specular attenuation.
			if(normal_light > 0.0) specularFactor = pow(normal_halfVector, gl_FrontMaterial.shininess);

			if(gl_LightSource[current].spotCutoff != 180.0) {
				// SpotlightSource
				float spotCos = dot(-light_view, normalize(gl_LightSource[current].spotDirection));
				float spotCosCutoff = cos(radians(gl_LightSource[current].spotCutoff));
				
				// Check whether outside spotlight cone, and attenuate it.
				if(spotCos < spotCosCutoff) attenuation = 0.0;
				else attenuation = attenuation * pow(spotCos, gl_LightSource[current].spotExponent);
			}

			ambients += gl_LightSource[current].ambient;
			diffuses += gl_LightSource[current].diffuse * normal_light * attenuation;
			speculars += gl_LightSource[current].specular * specularFactor * attenuation;
		}
	}

	// Add up to color.
	gl_FrontColor = gl_FrontMaterial.emission + gl_FrontMaterial.ambient * ambients + gl_FrontMaterial.diffuse * diffuses
			+ gl_FrontMaterial.specular * speculars;
	gl_BackColor = gl_FrontColor;
}
