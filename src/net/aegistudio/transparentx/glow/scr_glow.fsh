varying vec2 unifiedCoord;
uniform sampler2D glowMapping;
const float illuminanceThreshold = 0.5;

uniform float radius;
uniform float amplification;
uniform float sampleDirections;

uniform vec4 zVector;

float illuminance() {
		vec4 sample = texture2D(glowMapping, unifiedCoord);
		if(length(sample.xyz) != 0.0) return length(sample);
		
		sample = sample
				+ 0.5 * texture2D(glowMapping, unifiedCoord + vec2(0.01, 0.01))
				+ 0.5 * texture2D(glowMapping, unifiedCoord + vec2(0.01, -0.01))
				+ 0.5 * texture2D(glowMapping, unifiedCoord + vec2(-0.01, -0.01))
				+ 0.5 * texture2D(glowMapping, unifiedCoord + vec2(-0.01, 0.01));
		sample /= 3.0;
		
		return length(sample.xyz);
}

void main() {
		vec4 sample = texture2D(glowMapping, unifiedCoord);
		
		if(illuminance() > illuminanceThreshold) {
			// It's a glow body, we will override the lightValue.
			gl_FragColor = sample;
		}
		else {
			// It may be surrounded by a glow body, please calculate the lightvalue for it.
			gl_FragColor += sample;
			
			for(float sampX = 1.0; sampX <= sampleDirections; sampX += 1.0) 
				for(float sampY = 1.0; sampY <= sampleDirections; sampY += 1.0) {
					vec2 sampleVector = vec2(sampX * radius / sampleDirections, sampY * radius / sampleDirections);
					float ur = length(sampleVector);
					float decay = pow(dot(vec4(1.0, ur, ur * ur, 0.0), zVector), zVector.w);
					float attenuations = amplification / (decay * 4.0);
					
					vec4 sampledColor = vec4(0.0);
					
					sampledColor += texture2D(glowMapping, vec2(+sampleVector.x, +sampleVector.y) + unifiedCoord);
					sampledColor += texture2D(glowMapping, vec2(-sampleVector.x, +sampleVector.y) + unifiedCoord);
					sampledColor += texture2D(glowMapping, vec2(-sampleVector.x, -sampleVector.y) + unifiedCoord);
					sampledColor += texture2D(glowMapping, vec2(+sampleVector.x, -sampleVector.y) + unifiedCoord);
					
					gl_FragColor += sampledColor * amplification / (sampleDirections * sampleDirections);
				}
		}
}