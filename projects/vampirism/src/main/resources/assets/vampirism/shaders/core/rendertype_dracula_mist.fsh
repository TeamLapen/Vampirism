#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:globals.glsl>

in vec2 texCoord0;
in float sphericalVertexDistance;
in float cylindricalVertexDistance;

out vec4 fragColor;

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), u.x),
               mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), u.x), u.y);
}

float fbm(vec2 p) {
    float value = 0.0;
    float amplitude = 0.5;
    for (int i = 0; i < 5; i++) {
        value += amplitude * noise(p);
        p = p * 2.03 + vec2(17.3, 9.1);
        amplitude *= 0.55;
    }
    return value;
}

void main() {
    vec2 centered = texCoord0 - 0.5;
    float dist = length(centered) * 2.0;

    float t = GameTime * 400.0;
    float n = fbm(texCoord0 * 6.0 + vec2(t * 0.35, -t * 0.2));
    n += 0.5 * fbm(texCoord0 * 12.0 - vec2(t * 0.15, t * 0.4));
    n /= 1.5;

    // soft circular falloff, eaten away by noise at the rim
    float edge = 1.0 - smoothstep(0.3, 1.0, dist + (n - 0.5) * 0.6);
    float alpha = edge * (0.5 + 0.5 * n);
    if (alpha < 0.02) {
        discard;
    }

    vec3 color = mix(vec3(0.01, 0.0, 0.02), vec3(0.13, 0.02, 0.06), n);
    fragColor = apply_fog(vec4(color, alpha), sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
}
