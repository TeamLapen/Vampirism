#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 Position;
in vec2 UV0;

out vec2 texCoord0;
out vec3 vPosition;
out float sphericalVertexDistance;
out float cylindricalVertexDistance;

/**
 * Expands a static unit quad (corners at +-1) into a camera-facing square centered on a volume, large enough to
 * bound it. Shared by every raymarched volume in the mod - rendertype_mist and rendertype_aura_of_darkness - all
 * of which bound themselves to a sphere at TextureMat[0].xyz with radius TextureMat[0].w. The remaining
 * per-instance parameters also ride in TextureMat; see each fragment shader for its own layout.
 *
 * The quad's axes are derived here rather than passed in: building them perpendicular to the view ray towards
 * the center is what lets the fragment shader solve the ray-sphere intersection from a fragment's radial offset
 * alone, and deriving them frees those matrix slots for the fragment shader's own parameters.
 */
void main() {
    vec3 center = TextureMat[0].xyz;
    float boundRadius = TextureMat[0].w;

    vec3 forward = normalize(center);
    vec3 right = cross(vec3(0.0, 1.0, 0.0), forward);
    right = length(right) < 1.0e-4 ? vec3(1.0, 0.0, 0.0) : normalize(right);
    vec3 up = cross(forward, right);

    vec3 worldPos = center + right * (Position.x * boundRadius) + up * (Position.y * boundRadius);

    gl_Position = ProjMat * ModelViewMat * vec4(worldPos, 1.0);
    texCoord0 = UV0;
    // Camera-relative position with world axes - the camera sits at the origin, so this doubles as the view ray.
    vPosition = worldPos;

    sphericalVertexDistance = fog_spherical_distance(worldPos);
    cylindricalVertexDistance = fog_cylindrical_distance(worldPos);
}
