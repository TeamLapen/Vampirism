#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:globals.glsl>

// texCoord0.x: across the beam ribbon [0..1], texCoord0.y: distance along the beam in blocks
in vec2 texCoord0;
in float sphericalVertexDistance;
in float cylindricalVertexDistance;

out vec4 fragColor;

void main() {
    float across = texCoord0.x;
    float along = texCoord0.y;

    // blood streaks flowing from the victim (high y) towards Dracula (y = 0)
    float t = GameTime * 1200.0;
    float flow = fract(along * 0.6 + t);
    float streak = smoothstep(0.0, 0.25, flow) * (1.0 - smoothstep(0.45, 0.95, flow));

    // bright core in the middle of the ribbon
    float core = 1.0 - abs(across - 0.5) * 2.0;
    float pulse = 0.8 + 0.2 * sin(GameTime * 3000.0 + along * 2.5);

    float alpha = core * (0.3 + 0.7 * streak) * pulse;
    if (alpha < 0.02) {
        discard;
    }

    vec3 color = mix(vec3(0.25, 0.0, 0.03), vec3(0.85, 0.08, 0.12), streak * core);
    fragColor = apply_fog(vec4(color, alpha), sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
}
