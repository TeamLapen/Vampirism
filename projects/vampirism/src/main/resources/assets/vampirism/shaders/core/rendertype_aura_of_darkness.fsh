#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:globals.glsl>
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

uniform sampler2D DepthSampler;
// Bound by VolumetricBillboards#draw for every volumetric pipeline it draws, mist included, but this flat-ring
// shader has no noise field to sample - it is declared only so the pipeline matches that shared draw path.
uniform sampler2D NoiseSampler;

/**
 * Per-instance aura parameters, written by AuraOfDarknessRenderer into the TextureMat slot of the
 * DynamicTransforms uniform - the same trick rendertype_mist uses, so this pass needs no GPU buffer of its own.
 *
 * column 0   xyz = camera-relative center of the aura, w = horizontal semi-axis of the support ellipsoid
 * column 1   x = horizontal radius (world x and z), y = vertical radius, w = vertical semi-axis of the support
 *            ellipsoid
 * column 2   x = fade envelope
 * column 3   w = pass resolution scale
 *
 * The support ellipsoid in the w slots of columns 0 and 1 is the shared contract with volumetric_billboard.vsh:
 * the border is empty outside it, so it is both what the quad is fitted to and what the ring is clipped to. It
 * is simply the radii scaled by SUPPORT_SCALE. Unlike mist this volume is pinned to its entity, so it needs no
 * velocity, heading or accumulated flow.
 */
#define Center TextureMat[0]
#define Shape TextureMat[1]
#define Envelope TextureMat[2]
#define Pass TextureMat[3]

in vec3 vPosition;
in float sphericalVertexDistance;
in float cylindricalVertexDistance;

out vec4 fragColor;

/**
 * The border is a single ring evaluation per fragment rather than a raymarched volume: for each screen pixel,
 * find the point on the view ray closest to the entity's center, in the space where dividing by the radii turns
 * the silhouette into a unit sphere. A ray aimed at the body passes well inside that sphere, closer to the center
 * than a ray grazing the silhouette, which passes almost exactly over its surface. That closest-approach distance
 * is what separates "inside the silhouette" from "right at its edge" without a march.
 *
 * Distances here are normalized by the radii. The whole silhouette (0 - 1.0) is filled, tapering out into the
 * more solid border from RING_INNER to RING_PEAK, which itself fades away by RING_OUTER.
 */
const float RING_INNER = 0.85;
const float RING_PEAK = 1.0;
const float RING_OUTER = 1.15;
/** How strong the interior fill is relative to the border's peak opacity at the silhouette's edge. */
const float FILL_STRENGTH = 0.4;
/** Flat near-black color for the whole aura - deliberately a single solid tone, no gradient or noise. */
const vec3 BORDER_COLOR = vec3(0.02, 0.015, 0.03);
const float MAX_ALPHA = 0.35;
/** Distance in blocks over which the border tapers out as it approaches solid geometry, so it melts into a wall. */
const float DEPTH_SOFTNESS = 0.4;

/**
 * Distance from the camera to the solid geometry behind this fragment, measured along rayDir in blocks.
 * Reconstructed from the scene depth buffer, which this pass binds as a sampler rather than as an attachment -
 * that is what allows the border to fade into walls and floors instead of being sliced by a depth test, and what
 * stops it from being drawn on the far side of the entity's own body.
 */
float sceneDistance(vec2 screenUV, vec3 rayDir) {
    float depth = texture(DepthSampler, screenUV).r;
    if (depth >= 1.0) {
        return 1.0e9; // sky, or nothing drawn - never occludes
    }
    // For a perspective projection ndcZ = (A * z + B) / -z, so the distance along the camera's forward axis
    // falls straight out of the two matrix entries.
    float ndcZ = depth * 2.0 - 1.0;
    float forwardDist = ProjMat[3].z / (ndcZ + ProjMat[2].z);
    // The camera's forward axis, in the camera-relative world space these positions live in, is the negated
    // third row of the view matrix.
    vec3 camForward = -vec3(ModelViewMat[0].z, ModelViewMat[1].z, ModelViewMat[2].z);
    // Rays away from the center of the screen travel further before reaching the same forward depth.
    return forwardDist / max(dot(rayDir, camForward), 1.0e-4);
}

void main() {
    vec3 radii = vec3(Shape.x, Shape.y, Shape.x);
    float fade = Envelope.x;
    if (radii.x <= 0.001 || fade <= 0.0) {
        discard;
    }

    vec3 viewDir = normalize(vPosition);

    // Ray against the entity-centered ellipsoid, solved in the space where dividing by the radii turns it into
    // a unit sphere - see the comment above for why the closest approach in that space is what the ring wants.
    vec3 rayOrigin = -Center.xyz / radii;
    vec3 rayStep = viewDir / radii;
    float a = dot(rayStep, rayStep);
    float b = dot(rayOrigin, rayStep);
    float t = max(-b / a, 0.0);

    // Never draw past solid geometry - including the entity the border surrounds, which is what keeps the far
    // side of the ring from showing through it.
    float sceneDist = sceneDistance(gl_FragCoord.xy * Pass.w / ScreenSize, viewDir);
    if (t > sceneDist) {
        discard;
    }

    vec3 closest = rayOrigin + t * rayStep;
    float d = length(closest);

    float border = smoothstep(RING_INNER, RING_PEAK, d);
    float coverage = 1.0 - smoothstep(RING_PEAK, RING_OUTER, d);
    float ring = mix(FILL_STRENGTH, 1.0, border) * coverage;
    // Soft taper as the ring approaches solid geometry, so it melts into the surface instead of ending in a hard
    // line.
    ring *= clamp((sceneDist - t) / DEPTH_SOFTNESS, 0.0, 1.0);
    if (ring <= 0.001) {
        discard;
    }

    float alpha = ring * fade * MAX_ALPHA;

    vec4 fogged = apply_fog(vec4(BORDER_COLOR, alpha), sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
    // Premultiplied on the way out, so the result survives being accumulated into an offscreen target and
    // composited back. Identical to what the straight-alpha blend produced.
    fragColor = vec4(fogged.rgb * alpha, alpha);
}
