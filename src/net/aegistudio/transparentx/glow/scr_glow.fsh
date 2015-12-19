varying vec2 unifiedCoord;
uniform sampler2D glowMapping;
const float illuminanceThreshold = 0.5;

const float radium = 0.05;
const float sampleDirections = 20.0;
const float samplePoints = 10.0;

void main() {
		vec4 sample = texture2D(glowMapping, unifiedCoord);
		float illuminance = length(sample.xyz);
		if(illuminance > illuminanceThreshold) {
			// It's a glow body, we will override the lightValue.
			gl_FragColor += sample;
		}
		else {
			// It may be surrounded by a glow body, please calculate the lightvalue for it.
			for(float r = 1.0; r < samplePoints; r += 1.0) {
				float attenuations = 2.0 / pow(2000.0 * radium * r / samplePoints, 1.9);
				
				for(float theta = 0.0; theta < 360.0; theta += 360.0 / sampleDirections) {
					vec2 sampleVector = unifiedCoord + vec2(radium * cos(theta), radium * sin(theta));
					if(sampleVector.x > 1.0 || sampleVector.y > 1.0) continue;
					if(sampleVector.x < 0.0 || sampleVector.y < 0.0) continue;
					gl_FragColor += texture2D(glowMapping, sampleVector) * attenuations;
				}
			}
				
		}
}