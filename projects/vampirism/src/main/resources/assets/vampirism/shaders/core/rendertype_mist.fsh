#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:globals.glsl>
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

uniform sampler2D DepthSampler;

/**
 * Per-instance mist parameters, written by MistRenderer into the TextureMat slot of the DynamicTransforms
 * uniform. Riding along in that existing per-draw uniform means this pass needs no hand-managed GPU buffer of
 * its own - it reuses the same ring buffer vanilla uses for every other draw.
 *
 * column 0   xyz = camera-relative center of the cloud, w = bounding sphere radius (also the billboard half-size)
 * column 1   xyz = accumulated flow offset in blocks, w = horizontal radius (world x and z)
 * column 2   xy  = smoothed horizontal heading (unit), z = smoothed speed in blocks/tick, w = vertical radius
 *            Horizontal radius > vertical is what gives the flattened, wider-than-tall puff. Both are world-axis
 *            aligned; the billboard axes are derived in the vertex shader and never used here.
 * column 3   x = fade envelope, y = trailing stretch. The stretch is computed on the Java side rather than here
 *            so the bounding radius it feeds into stays in sync with the quad the vertex shader builds.
 */
#define Center TextureMat[0]
#define Flow TextureMat[1]
#define Motion TextureMat[2]
#define Shape TextureMat[3]

in vec2 texCoord0;
in vec3 vPosition;
in float sphericalVertexDistance;
in float cylindricalVertexDistance;

out vec4 fragColor;

// Raised alongside the wide, soft falloff band below - a softer density profile integrates to less opacity, so
// this keeps the cloud reading as solid through the middle rather than washing out.
const float ABSORPTION = 3.0;
const float NOISE_SCALE = 0.9;
const float DETAIL_SCALE = 2.6;
// Neutral gray smoke: near-black core through to pale gray where the volume thins out. Every constant is
// deliberately achromatic - equal channels - so the cloud carries no color cast in any lighting.
const vec3 DARK_COLOR = vec3(0.07);
const vec3 LIGHT_COLOR = vec3(0.72);
const vec3 RIM_COLOR = vec3(0.88);
const float WARP_STRENGTH = 0.55;
// Base rate at which the internal noise drifts, in units per second. Kept deliberately low: the volume should
// read as slowly roiling, not as a boiling texture that competes with the entity's own motion.
const float TURBULENCE_BASE_SPEED = 0.09;
// How much faster it churns per block/tick of movement, and the speed at which that saturates. Without the cap,
// a sprinting entity would whip the volume into a blur.
const float TURBULENCE_SPEED_SCALE = 0.6;
const float TURBULENCE_SPEED_CAP = 0.4;
// Distance in blocks over which density tapers out as the raymarch approaches solid geometry.
const float DEPTH_SOFTNESS = 0.5;

float hash(vec3 p) {
    p = fract(p * vec3(0.1031, 0.1030, 0.0973));
    p += dot(p, p.yzx + 33.33);
    return fract((p.x + p.y) * p.z);
}

// Trilinear value noise.
float valueNoise(vec3 p) {
    vec3 i = floor(p);
    vec3 f = fract(p);
    vec3 u = f * f * (3.0 - 2.0 * f);

    float n000 = hash(i + vec3(0.0, 0.0, 0.0));
    float n100 = hash(i + vec3(1.0, 0.0, 0.0));
    float n010 = hash(i + vec3(0.0, 1.0, 0.0));
    float n110 = hash(i + vec3(1.0, 1.0, 0.0));
    float n001 = hash(i + vec3(0.0, 0.0, 1.0));
    float n101 = hash(i + vec3(1.0, 0.0, 1.0));
    float n011 = hash(i + vec3(0.0, 1.0, 1.0));
    float n111 = hash(i + vec3(1.0, 1.0, 1.0));

    float nx00 = mix(n000, n100, u.x);
    float nx10 = mix(n010, n110, u.x);
    float nx01 = mix(n001, n101, u.x);
    float nx11 = mix(n011, n111, u.x);

    return mix(mix(nx00, nx10, u.y), mix(nx01, nx11, u.y), u.z);
}

float fbm(vec3 p) {
    float value = 0.0;
    float amplitude = 0.55;
    for (int i = 0; i < 4; i++) {
        value += amplitude * valueNoise(p);
        p = p * 2.02 + vec3(11.1, 5.3, 7.7);
        amplitude *= 0.5;
    }
    return value;
}

/**
 * Distance from the camera to the solid geometry behind this fragment, measured along rayDir in blocks.
 * Reconstructed from the scene depth buffer, which this pass binds as a sampler rather than as an attachment -
 * that is what allows the volume to fade into walls and floors instead of being sliced by a depth test.
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
    float boundR = Center.w;
    float radiusXZ = Flow.w;
    float radiusY = Motion.w;
    float fade = Shape.x;
    if (boundR <= 0.001 || fade <= 0.0) {
        discard;
    }

    // The billboard is a square bounding a sphere, so the corners fall outside it and are rejected before any
    // raymarching happens.
    vec2 centeredUV = texCoord0 * 2.0 - 1.0;
    float radialDist = length(centeredUV) * boundR;
    if (radialDist > boundR) {
        discard;
    }

    float q = sphericalVertexDistance;
    if (q < 0.001) {
        discard;
    }

    // Because Right/Up are perpendicular to the direction of Center, the fragment's radial offset and its
    // distance from the camera are enough for an exact ray-sphere intersection: trueDist is the perpendicular
    // distance from this camera ray to the center, t0 the ray parameter of the closest approach.
    float trueDist = radialDist * sqrt(max(q * q - radialDist * radialDist, 0.0)) / q;
    if (trueDist > boundR) {
        discard;
    }
    float t0 = q - (radialDist * radialDist) / q;
    float halfChord = sqrt(max(boundR * boundR - trueDist * trueDist, 0.0));

    vec3 viewDir = vPosition / q;

    float tStart = max(t0 - halfChord, 0.0);
    float tEnd = t0 + halfChord;
    // Never march past solid geometry, so the cloud cannot bleed through a wall it is standing behind.
    float sceneDist = sceneDistance(gl_FragCoord.xy / ScreenSize, viewDir);
    tEnd = min(tEnd, sceneDist);
    if (tEnd <= tStart) {
        discard;
    }

    float stepSize = (tEnd - tStart) / float(STEPS);
    // Per-pixel dither on the ray start hides the banding the step size would otherwise make visible.
    tStart += hash(vec3(gl_FragCoord.xy, 0.0)) * stepSize;

    vec2 velDir = Motion.xy;
    float speed = Motion.z;
    float trailFactor = 1.0 + Shape.y;

    // GameTime is a fraction of the 24000-tick day, so this is simply elapsed seconds.
    float seconds = GameTime * 1200.0;
    // Idle animation: a slow, low-amplitude sway that keeps the volume alive while standing still, independent
    // of the turbulence drift below (which also runs at rest, just without the sway).
    vec3 idleOffset = vec3(
        sin(seconds * 1.10),
        sin(seconds * 0.77 + 1.3),
        cos(seconds * 0.93)
    ) * (0.08 * radiusXZ);
    // Movement animation, part one: faster movement churns the internal noise faster, up to a cap.
    float drift = seconds * TURBULENCE_BASE_SPEED * (1.0 + min(speed, TURBULENCE_SPEED_CAP) * TURBULENCE_SPEED_SCALE);
    vec3 driftOffset = vec3(drift * 0.4, -drift * 0.25, drift * 0.3) + idleOffset;

    float transmittance = 1.0;
    vec3 accumColor = vec3(0.0);
    for (int i = 0; i < STEPS; i++) {
        float t = tStart + (float(i) + 0.5) * stepSize;
        vec3 samplePos = viewDir * t;

        // Position relative to the cloud, normalized by the radii. These positions are already in world axes
        // (camera-relative, but unrotated), so the flattening stays pinned to world up instead of swinging
        // around with the camera - which is what a billboard-space ellipsoid would do.
        vec3 rel = samplePos - Center.xyz;
        vec3 local = vec3(rel.x / radiusXZ, rel.y / radiusY, rel.z / radiusXZ);

        // Movement animation, part two: stretch the trailing side of the ellipsoid so the mist streams out
        // behind fast movement like a wake, while the leading side stays compact.
        float trailProj = dot(vec2(local.x, local.z), velDir);
        float localTrail = 1.0 + (trailFactor - 1.0) * clamp(-trailProj, 0.0, 1.0);
        float distToCenter = length(local) / localTrail;

        // Movement animation, part one: the noise field is sampled offset by the accumulated flow, so it streams
        // backwards through the volume as the entity travels. Because the offset is integrated over time on the
        // Java side rather than derived from the current heading, turning bends the flow around smoothly instead
        // of snapping the whole interior to a new direction.
        //
        // Everything below samples relative to the cloud - never at the camera-relative samplePos. Sampling at
        // samplePos would drag the noise field through the volume whenever the camera moved, making the interior
        // boil under camera motion alone. Staying cloud-local also keeps the coordinates small, which the value
        // noise is much better conditioned for.
        vec3 flowPos = rel + Flow.xyz;

        // Domain warp: displace the sample before evaluating the shape, so puffs curl and drift instead of the
        // boundary just breathing in and out radially. This is what turns a lumpy ellipsoid into a billowy cloud.
        vec3 warp = vec3(
            valueNoise(flowPos * 0.09 + vec3(4.0, 90.0, 2.0)),
            valueNoise(flowPos * 0.09 + vec3(30.0, 1.0, 50.0)),
            valueNoise(flowPos * 0.09 + vec3(70.0, 40.0, 8.0))
        ) - 0.5;
        vec3 warpedPos = flowPos + warp * (WARP_STRENGTH * radiusXZ);

        // Two-octave shape noise perturbs the boundary radius per direction, so the silhouette is a loose,
        // uneven puff. The falloff band is wide on purpose: it is measured in radius-normalized units, so on the
        // flattened vertical axis a narrow band would collapse to a few centimetres of world-space falloff and
        // read as a hard edge.
        float shapeNoise = 0.65 * valueNoise(warpedPos * 0.22 + vec3(19.0, 7.0, 31.0))
                         + 0.35 * valueNoise(warpedPos * 0.11 + vec3(-8.0, 44.0, 3.0));
        float localRadius = mix(0.75, 1.15, shapeNoise);
        float edge = 1.0 - smoothstep(localRadius * 0.25, localRadius * 1.15, distToCenter);
        // Guaranteed fade to zero before the bounding sphere the march actually reaches, whatever the noise did,
        // so the padded quad's silhouette can never show as a hard cut.
        edge *= 1.0 - smoothstep(0.85, 1.0, length(rel) / boundR);

        // Base cloud shape eroded by a finer detail pass, the classic two-layer cloud-noise trick.
        float base = fbm(warpedPos * NOISE_SCALE + driftOffset);
        float detail = valueNoise(warpedPos * DETAIL_SCALE - driftOffset * 1.3);
        float n = clamp(base - 0.25 * detail, 0.0, 1.0);
        float density = smoothstep(0.1, 0.75, n) * edge * fade;

        // Soft-particle taper: thin the volume out as it approaches solid geometry so it melts into the surface.
        density *= clamp((sceneDist - t) / DEPTH_SOFTNESS, 0.0, 1.0);
        if (density <= 0.0) {
            continue;
        }

        // Fake top-light: samples where the volume thins out just above them are treated as more exposed to
        // light from above, and brightened toward LIGHT_COLOR.
        float aboveNoise = valueNoise((warpedPos + vec3(0.0, 0.35, 0.0)) * NOISE_SCALE + driftOffset);
        float topLight = clamp((n - aboveNoise) * 3.0 + 0.15, 0.0, 1.0);

        float sampleAlpha = 1.0 - exp(-density * ABSORPTION * stepSize);

        vec3 bodyColor = mix(DARK_COLOR, LIGHT_COLOR, density);
        bodyColor = mix(bodyColor, LIGHT_COLOR, topLight * 0.5);
        // Rim highlight peaks in the silhouette's soft transition (edge ~= 0.5), so only thin, low-density
        // wisps near the boundary catch it, like backlit fog.
        float rim = clamp(4.0 * edge * (1.0 - edge), 0.0, 1.0);
        vec3 sampleColor = mix(bodyColor, RIM_COLOR, rim * 0.55);

        accumColor += transmittance * sampleAlpha * sampleColor;
        transmittance *= (1.0 - sampleAlpha);

        if (transmittance < 0.02) {
            break;
        }
    }

    float alpha = 1.0 - transmittance;
    if (alpha < 0.02) {
        discard;
    }

    fragColor = apply_fog(vec4(accumColor, alpha), sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
}
