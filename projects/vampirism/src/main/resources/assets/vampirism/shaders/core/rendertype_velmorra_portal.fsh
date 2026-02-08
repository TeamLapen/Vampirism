#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:matrix.glsl>
#moj_import <minecraft:globals.glsl>

uniform sampler2D Sampler0;

in vec4 texProj0;
in float sphericalVertexDistance;
in float cylindricalVertexDistance;

out vec4 fragColor;

mat2 rotate(float angle) {
    float c = cos(angle);
    float s = sin(angle);
    return mat2(c, -s, s, c);
}

void main() {
    vec2 uv = texProj0.xy / texProj0.w;

    // Center the coordinates
    vec2 centered = uv - 0.5;

    // Calculate distance from center for the swirl effect
    float dist = length(centered);

    // Create multiple swirling layers
    vec3 color = vec3(0.0);

    for (int i = 0; i < 8; i++) {
        float layer = float(i + 1);

        // Rotation based on time and layer
        float angle = GameTime * 50 + layer * 0.8 + dist * 3.0;

        // Scale that gets smaller towards center
        float scale = 2.0 + layer * 0.5 - dist * 1.5;

        // Apply rotation and scaling
        vec2 rotated = rotate(angle) * centered * scale;

        // Translate based on time and layer
        rotated += vec2(GameTime * 50 + layer * 0.1, GameTime * 0.2 + layer * 0.05);

        // Sample texture
        vec3 layerColor = texture(Sampler0, rotated).rgb;

        // Blend with distance-based alpha for swirl effect
        float alpha = 1.0 / (1.0 + dist * 2.0) * (1.0 - layer / 10.0);
        color += layerColor * alpha;
    }

    fragColor = apply_fog(vec4(color, 1.0), sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
}