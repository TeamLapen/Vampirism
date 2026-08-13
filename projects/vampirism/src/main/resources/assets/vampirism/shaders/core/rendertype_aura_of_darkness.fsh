#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:globals.glsl>
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

uniform sampler2D DepthSampler;
uniform sampler2D NoiseSampler;

/**
 * Per-instance aura parameters, written by AuraOfDarknessRenderer into the TextureMat slot of the
 * DynamicTransforms uniform - the same trick rendertype_mist uses, so this pass needs no GPU buffer of its own.
 *
 * column 0   xyz = camera-relative center of the aura, w = horizontal semi-axis of the support ellipsoid
 * column 1   x = horizontal radius (world x and z), y = vertical radius, z = per-entity phase offset in seconds,
 *            w = vertical semi-axis of the support ellipsoid
 * column 2   x = fade envelope
 * column 3   w = pass resolution scale
 *
 * The support ellipsoid in the w slots of columns 0 and 1 is the shared contract with volumetric_billboard.vsh:
 * the shell is empty outside it, so it is both what the quad is fitted to and what the march is clipped to. It
 * is simply the radii scaled by SHELL_OUTER. Unlike mist this volume is pinned to its entity, so it needs no
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
 * The aura is a thin shell rather than a filled volume, so the entity inside it stays readable: a ray aimed at
 * the entity's body crosses the band perpendicularly and picks up barely any density, while a ray grazing the
 * silhouette travels along the band and accumulates far more. That limb brightening is the whole effect - a dark
 * halo hugging the outline, with only a faint veil over the body itself.
 *
 * Distances here are normalized by the radii, so the band is 0.68 - 1.02 of the ellipsoid's surface.
 */
const float SHELL_INNER = 0.68;
const float SHELL_PEAK = 0.90;
const float SHELL_OUTER = 1.02;
/**
 * Deliberately low. The aura has to read as "something is there" without hiding the entity or the world behind
 * it, so absorption is weak and the result is hard-capped below.
 */
const float ABSORPTION = 0.7;
const float MAX_ALPHA = 0.35;
/** Scale of the noise that breaks the shell into wisps, and how far it may thin the band out. */
const float NOISE_SCALE = 1.6;
const float WISP_FLOOR = 0.35;
/** Speed of the slow rotation about world up, and of the upward drift, in radians and blocks per second. */
const float SWIRL_SPEED = 0.15;
const float RISE_SPEED = 0.06;
// Near-black body shading toward a faint cool violet where the volume thins out, so the halo carries a hint of
// hue at its edge instead of reading as a flat gray smudge.
const vec3 CORE_COLOR = vec3(0.02);
const vec3 WISP_COLOR = vec3(0.11, 0.07, 0.18);
/** Distance in blocks over which density tapers out as the raymarch approaches solid geometry. */
const float DEPTH_SOFTNESS = 0.4;

const float NOISE_SIZE = 256.0;
const vec2 NOISE_SLICE_STEP = vec2(37.0, 17.0);

/**
 * 3D value noise from one texture fetch. The sheet holds a white-noise lattice laid out so that a single
 * filtered fetch returns the x/y interpolation of both slices bracketing p.z at once - see VolumetricNoise for
 * how it is packed, and rendertype_mist for the variant that pulls two uncorrelated fields out of it.
 * Smoothstepping the fractional part before the fetch is what turns the texture unit's linear filter into the
 * smooth interpolation value noise wants, leaving only the blend along z to do here.
 */
float valueNoise(vec3 p) {
    vec3 i = floor(p);
    vec3 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    vec2 uv = i.xy + NOISE_SLICE_STEP * i.z + f.xy + 0.5;
    vec4 texel = texture(NoiseSampler, uv / NOISE_SIZE);
    return mix(texel.g, texel.r, f.z);
}

// Three octaves is enough here: the shell is thin, so finer detail would not survive the band's own falloff.
float fbm(vec3 p) {
    float value = 0.0;
    float amplitude = 0.6;
    for (int i = 0; i < 3; i++) {
        value += amplitude * valueNoise(p);
        p = p * 2.03 + vec3(9.7, 3.1, 6.5);
        amplitude *= 0.5;
    }
    return value;
}

/**
 * Distance from the camera to the solid geometry behind this fragment, measured along rayDir in blocks.
 * Reconstructed from the scene depth buffer, which this pass binds as a sampler rather than as an attachment -
 * that is what allows the aura to fade into walls and floors instead of being sliced by a depth test, and what
 * stops the raymarch at the entity's own surface so only the part of the shell in front of it is drawn.
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
    vec3 semi = vec3(Center.w, Shape.w, Center.w);
    float radiusXZ = Shape.x;
    float radiusY = Shape.y;
    float fade = Envelope.x;
    if (semi.x <= 0.001 || fade <= 0.0) {
        discard;
    }

    vec3 viewDir = normalize(vPosition);

    // Ray against the support ellipsoid, solved in the space where dividing by the semi-axes turns it into a
    // unit sphere. Clipping the march to the ellipsoid rather than to a sphere around it keeps the steps inside
    // the shell, which matters more here than for mist: the shell is thin, so a padded bound spends most of its
    // samples in space that can never contribute.
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

    // Never march past solid geometry - including the entity the aura surrounds, which is what keeps the far
    // half of the shell from showing through it.
    float sceneDist = sceneDistance(gl_FragCoord.xy * Pass.w / ScreenSize, viewDir);
    tEnd = min(tEnd, sceneDist);
    if (tEnd <= tStart) {
        discard;
    }

    float stepSize = (tEnd - tStart) / float(STEPS);
    // Per-pixel dither on the ray start hides the banding the step size would otherwise make visible. Sampling
    // the noise sheet at texel centres gives a white-noise value per pixel for the price of one fetch.
    tStart += texture(NoiseSampler, gl_FragCoord.xy / NOISE_SIZE).g * stepSize;

    // GameTime is a fraction of the 24000-tick day, so this is simply elapsed seconds. The per-instance phase
    // keeps nearby auras from swirling in lockstep.
    float seconds = GameTime * 1200.0 + Shape.z;
    float angle = seconds * SWIRL_SPEED;
    float swirlCos = cos(angle);
    float swirlSin = sin(angle);
    float rise = seconds * RISE_SPEED;

    float transmittance = 1.0;
    vec3 accumColor = vec3(0.0);
    for (int i = 0; i < STEPS; i++) {
        float t = tStart + (float(i) + 0.5) * stepSize;
        vec3 samplePos = viewDir * t;

        // Position relative to the aura, normalized by the radii. These positions are already in world axes
        // (camera-relative, but unrotated), so the flattening stays pinned to world up instead of swinging
        // around with the camera.
        vec3 rel = samplePos - Center.xyz;
        vec3 local = vec3(rel.x / radiusXZ, rel.y / radiusY, rel.z / radiusXZ);
        float d = length(local);

        // Reaches zero exactly at SHELL_OUTER, which is why the support ellipsoid can be that and still never
        // show its own silhouette. Rejecting here, before the noise, is what makes the hollow interior the
        // march crosses nearly free.
        float shell = smoothstep(SHELL_INNER, SHELL_PEAK, d) * (1.0 - smoothstep(SHELL_PEAK, SHELL_OUTER, d));
        if (shell <= 0.0) {
            continue;
        }

        // The noise is sampled in aura-local space, slowly turning about world up and drifting downwards through
        // the volume, so the wisps creep around the entity. Sampling relative to the aura rather than at the
        // camera-relative position is what stops the interior boiling under camera motion alone.
        vec3 noisePos = vec3(
            swirlCos * rel.x - swirlSin * rel.z,
            rel.y - rise,
            swirlSin * rel.x + swirlCos * rel.z
        );
        // Floored rather than gated to zero: fully carving the band away leaves gaps that read as flicker on a
        // shell this thin, so the noise only thins it.
        float wisp = mix(WISP_FLOOR, 1.0, smoothstep(0.25, 0.75, fbm(noisePos * NOISE_SCALE)));

        float density = shell * wisp * fade;
        // Soft-particle taper: thin the volume out as it approaches solid geometry so it melts into the surface.
        density *= clamp((sceneDist - t) / DEPTH_SOFTNESS, 0.0, 1.0);
        if (density <= 0.0) {
            continue;
        }

        float sampleAlpha = 1.0 - exp(-density * ABSORPTION * stepSize);
        vec3 sampleColor = mix(WISP_COLOR, CORE_COLOR, density);

        accumColor += transmittance * sampleAlpha * sampleColor;
        transmittance *= (1.0 - sampleAlpha);
    }

    float coverage = 1.0 - transmittance;
    if (coverage < 0.01) {
        discard;
    }

    // Un-premultiply before capping: the cap has to bite on the alpha alone, or clamping it would darken the
    // colour along with it and turn the rim into a hard band.
    vec3 color = accumColor / coverage;
    float alpha = min(coverage, MAX_ALPHA * fade);

    vec4 fogged = apply_fog(vec4(color, alpha), sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
    // Premultiplied on the way out, so the result survives being accumulated into an offscreen target and
    // composited back. Identical to what the straight-alpha blend produced.
    fragColor = vec4(fogged.rgb * alpha, alpha);
}
