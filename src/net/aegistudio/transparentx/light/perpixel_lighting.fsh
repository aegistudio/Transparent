/**
 * Per-pixel Lighting (Vertex Shader)
 * This lighting shader treat every pixels
 * on rendered fragment as a shading unit.
 * There're some sharing variables here, for
 * the convenience of shading extension.
 */
 
varying vec4 _viewVector_interpolate;
varying vec3 _normal_interpolate;

vec4 _viewVector;
vec3 _normal;

vec4 _ambient_response;
vec4 _diffuse_response;
vec4 _specular_response;
vec4 _emission;
float _shininess;

void main() {
	// Redundant normal processing.
	_normal = _normal_interpolate;
	_viewVector = _viewVector_interpolate;
	vec3 eyeVector = vec3(_viewVector) / _viewVector.w;
	
	_ambient_response = gl_FrontMaterial.ambient;
	_diffuse_response = gl_FrontMaterial.diffuse;
	_specular_response = gl_FrontMaterial.specular;
	_emission = gl_FrontMaterial.emission;
	_shininess= gl_FrontMaterial.shininess;

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
			normal_light = max(0.0, dot(_normal, lightVector));
			normal_halfVector = max(0.0, dot(_normal, vec3(gl_LightSource[current].halfVector)));
			if(normal_light > 0.0) specularFactor = pow(normal_halfVector, _shininess);

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
			normal_light = max(0.0, dot(_normal, light_view));
			normal_halfVector = max(0.0, dot(_normal, halfVector));

			// Calculate factor for specular attenuation.
			if(normal_light > 0.0) specularFactor = pow(normal_halfVector, _shininess);

			if(gl_LightSource[current].spotCutoff != 180.0) {
				// SpotlightSource
				float spotCos = dot(-light_view, normalize(gl_LightSource[current].spotDirection));
				float spotCosCutoff = cos(radians(gl_LightSource[current].spotCutoff));
				
				// Check whether outside spotlight cone, and attenuate it.
				// There's some problem when we check the value of spotExponent (always 0), so it won't be calculated.
				if(spotCos < spotCosCutoff) attenuation = 0.0;
				else attenuation = attenuation * pow(spotCos, gl_LightSource[current].spotExponent);
			}

			ambients += gl_LightSource[current].ambient;
			diffuses += gl_LightSource[current].diffuse * normal_light * attenuation;
			speculars += gl_LightSource[current].specular * specularFactor * attenuation;
		}
	}

	// Add up to color.
	gl_FragColor = _emission + _ambient_response * ambients
			+ _diffuse_response * diffuses
			+ _specular_response * speculars;
}
