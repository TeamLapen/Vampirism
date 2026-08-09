#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:globals.glsl>
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

uniform sampler2D DepthSampler;
uniform sampler2D NoiseSampler;

/**
 * Per-instance mist parameters, written by MistRenderer into the TextureMat slot of the DynamicTransforms
 * uniform. Riding along in that existing per-draw uniform means this pass needs no hand-managed GPU buffer of
 * its own - it reuses the same ring buffer vanilla uses for every other draw.
 *
 * column 0   xyz = camera-relative center of the cloud, w = horizontal semi-axis of the support ellipsoid
 * column 1   xyz = accumulated flow offset in blocks, w = vertical semi-axis of the support ellipsoid
 * column 2   xy  = smoothed horizontal heading (unit), z = smoothed speed in blocks/tick, w = horizontal radius
 * column 3   x = fade envelope, y = trailing stretch, z = vertical radius, w = pass resolution scale
 *
 * The support ellipsoid in columns 0 and 1 is the shared contract with volumetric_billboard.vsh: the region
 * outside it is provably empty, so it is both what the quad is fitted to and what the march is clipped to. It
 * is the shape radii scaled by MAX_LOCAL below and by the trailing stretch. The shape radii themselves stay
 * separate because the density field is expressed in units of them. Horizontal radius > vertical is what gives
 * the flattened, wider-than-tall puff; both are world-axis aligned.
 *
 * The trailing stretch is computed on the Java side rather than here so the support radii it feeds into stay in
 * sync with the quad the vertex shader builds. The resolution scale turns gl_FragCoord into a screen UV when
 * this pass is rendered at reduced resolution; it is 1 when it is not.
 */
#define Center TextureMat[0]
#define Flow TextureMat[1]
#define Motion TextureMat[2]
#define Shape TextureMat[3]

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

/**
 * The largest distToCenter that can carry any density: the noise-perturbed localRadius peaks at 1.15 and its
 * falloff band ends at 1.15 times that. Beyond it the cloud is empty whatever the noise does, which is what
 * makes both the support ellipsoid and the per-step rejection below exact rather than approximate.
 */
const float MAX_LOCAL = 1.3225;

const float NOISE_SIZE = 256.0;
const vec2 NOISE_SLICE_STEP = vec2(37.0, 17.0);

/**
 * Two independent 3D value noise fields, one texture fetch.
 *
 * The sheet holds a white-noise lattice laid out so that one filtered fetch returns the x/y interpolation of
 * both slices bracketing p.z at once - see VolumetricNoise for how it is packed. Smoothstepping the fractional
 * part before the fetch is what turns the texture unit's linear filter into the smooth interpolation value
 * noise wants, leaving only the blend along z to do here.
 */
vec2 valueNoise2(vec3 p) {
    vec3 i = floor(p);
    vec3 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    vec2 uv = i.xy + NOISE_SLICE_STEP * i.z + f.xy + 0.5;
    vec4 texel = texture(NoiseSampler, uv / NOISE_SIZE);
    return mix(texel.ga, texel.rb, f.z);
}

float valueNoise(vec3 p) {
    return valueNoise2(p).x;
}

/**
 * Three octaves. A fourth would land at a period of about an eighth of a block - finer than the march resolves
 * at any quality level - so it only ever contributed aliasing. The starting amplitude is picked so the total
 * still sums to what the density thresholds below were tuned against.
 */
float fbm(vec3 p) {
    float value = 0.0;
    float amplitude = 0.589;
    for (int i = 0; i < 3; i++) {
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
    vec3 semi = vec3(Center.w, Flow.w, Center.w);
    float radiusXZ = Motion.w;
    float radiusY = Shape.z;
    float fade = Shape.x;
    if (semi.x <= 0.001 || fade <= 0.0) {
        discard;
    }

    vec3 viewDir = normalize(vPosition);

    // Ray against the support ellipsoid, solved in the space where dividing by the semi-axes turns it into a
    // unit sphere. Clipping the march to the ellipsoid rather than to a sphere around it is what keeps the
    // samples inside the cloud: the sphere the old bound used held nearly twice the volume, and every step
    // landing in that padding cost a full set of noise lookups to produce nothing.
    vec3 rayOrigin = -Center.xyz / semi;
    vec3 rayStep = viewDir / semi;
    float a = dot(rayStep, rayStep);
    float b = dot(rayOrigin, rayStep);
    float c = dot(rayOrigin, rayOrigin) - 1.0;
    float disc = b * b - a * c;
    if (disc <= 0.0) {
        discard; // ray misses the volume entirely - the quad's corners, mostly
    }
    float rootDisc = sqrt(disc);
    float tStart = max((-b - rootDisc) / a, 0.0);
    float tEnd = (-b + rootDisc) / a;

    // Never march past solid geometry, so the cloud cannot bleed through a wall it is standing behind.
    float sceneDist = sceneDistance(gl_FragCoord.xy * Shape.w / ScreenSize, viewDir);
    tEnd = min(tEnd, sceneDist);
    if (tEnd <= tStart) {
        discard;
    }

    float stepSize = (tEnd - tStart) / float(STEPS);
    // Per-pixel dither on the ray start hides the banding the step size would otherwise make visible. Sampling
    // the noise sheet at texel centres gives a white-noise value per pixel for the price of one fetch.
    tStart += texture(NoiseSampler, gl_FragCoord.xy / NOISE_SIZE).g * stepSize;

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
        // Out here the density is zero whatever the noise says, so reject before paying for any of it. This
        // costs a handful of arithmetic and is the difference between an empty sample being free and it
        // costing the full warp, shape and body lookups - which is what it used to cost, the old rejection
        // sitting below all of them.
        if (distToCenter >= MAX_LOCAL) {
            continue;
        }

        // Movement animation, part one: the noise field is sampled offset by the accumulated flow, so it streams
        // backwards through the volume as the entity travels. Because the offset is integrated over time on the
        // Java side rather than derived from the current heading, turning bends the flow around smoothly instead
        // of snapping the whole interior to a new direction.
        //
        // Everything below samples relative to the cloud - never at the camera-relative samplePos. Sampling at
        // samplePos would drag the noise field through the volume whenever the camera moved, making the interior
        // boil under camera motion alone.
        vec3 flowPos = rel + Flow.xyz;

        // Domain warp: displace the sample before evaluating the shape, so puffs curl and drift instead of the
        // boundary just breathing in and out radially. This is what turns a lumpy ellipsoid into a billowy
        // cloud. Two of the three offsets come from one fetch, the sheet carrying two uncorrelated fields.
        vec3 warp = vec3(
            valueNoise2(flowPos * 0.09 + vec3(4.0, 90.0, 2.0)),
            valueNoise(flowPos * 0.09 + vec3(30.0, 1.0, 50.0))
        ) - 0.5;
        vec3 warpedPos = flowPos + warp * (WARP_STRENGTH * radiusXZ);

        // Two-octave shape noise perturbs the boundary radius per direction, so the silhouette is a loose,
        // uneven puff. The falloff band is wide on purpose: it is measured in radius-normalized units, so on the
        // flattened vertical axis a narrow band would collapse to a few centimetres of world-space falloff and
        // read as a hard edge. It reaches zero at MAX_LOCAL, which is why the support ellipsoid can be exactly
        // that and still never show its own silhouette.
        float shapeNoise = 0.65 * valueNoise(warpedPos * 0.22 + vec3(19.0, 7.0, 31.0))
                         + 0.35 * valueNoise(warpedPos * 0.11 + vec3(-8.0, 44.0, 3.0));
        float localRadius = mix(0.75, 1.15, shapeNoise);
        float edge = 1.0 - smoothstep(localRadius * 0.25, localRadius * 1.15, distToCenter);
        // Second cheap rejection, now that the boundary is known but before the body noise is paid for.
        if (edge <= 0.0) {
            continue;
        }

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

    vec4 fogged = apply_fog(vec4(accumColor, alpha), sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
    // The pipeline blends premultiplied, both so the accumulation above - which is already premultiplied, being
    // a front-to-back march - stays correct through an offscreen target, and so the result is unchanged from
    // when it was blended straight: the multiply here is exactly what the straight-alpha blend was applying.
    fragColor = vec4(fogged.rgb * alpha, alpha);
}
