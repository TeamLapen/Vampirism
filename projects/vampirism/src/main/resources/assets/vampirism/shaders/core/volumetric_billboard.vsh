#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 Position;

out vec3 vPosition;
out float sphericalVertexDistance;
out float cylindricalVertexDistance;

/**
 * Expands a static unit quad (corners at +-1) into a camera-facing quad just large enough to cover a volume's
 * silhouette. Shared by every raymarched volume in the mod - rendertype_mist and rendertype_aura_of_darkness.
 *
 * All of them bound themselves to a world-axis-aligned ellipsoid centered at TextureMat[0].xyz, with horizontal
 * semi-axis TextureMat[0].w and vertical semi-axis TextureMat[1].w. Those two are the only slots the layouts
 * agree on; each fragment shader documents the rest of its own.
 *
 * Fitting the quad to that ellipsoid rather than to a sphere around it is worth a lot: these volumes are
 * deliberately flatter than they are wide, and every pixel the quad covers beyond the silhouette still pays for
 * a ray/ellipsoid test.
 *
 * The extent that has to be covered is not the ellipsoid's width at its center plane. A perspective camera sees
 * the silhouette where the tangent cone touches, which projects wider, and on an ellipsoid it also projects
 * off-center - the near cap swings the outline to one side. Both fall out of asking, for a quad axis A, how
 * large (P.A)/(P.forward) can get over the ellipsoid. Writing a surface point as center + semi*u for a unit u,
 * that ratio is bounded by t exactly when |semi*A - t*semi*forward| <= t*dist, and the extreme t are the roots
 * of that. The symmetric half-extent the quad needs is the larger root's magnitude, which is what falls out
 * below. For the horizontal axis the cross term vanishes - the two horizontal semi-axes are equal and this axis
 * has no vertical component - and it collapses to the familiar sphere form.
 */
void main() {
    vec3 center = TextureMat[0].xyz;
    vec3 semi = vec3(TextureMat[0].w, TextureMat[1].w, TextureMat[0].w);

    float dist = length(center);
    vec3 forward = dist > 1.0e-4 ? center / dist : vec3(0.0, 0.0, 1.0);
    vec3 semiForward = semi * forward;
    // Positive exactly while the camera sits outside the slab the ellipsoid spans along the view axis, which is
    // the condition for a tangent cone - and so for a bounded quad - to exist at all.
    float outside = dist * dist - dot(semiForward, semiForward);

    vec3 worldPos;
    if (outside <= 1.0e-3) {
        // Camera inside the volume, where no quad placed at the center can cover the silhouette - it wraps
        // around the viewer. Fall back to a full-screen quad and let the raymarch start at the near plane. The
        // rays still have to be real world-space rays, so they are rebuilt from the camera basis, which is the
        // transpose of the model view rotation, and the projection's half-angle tangents.
        vec3 camRight = vec3(ModelViewMat[0].x, ModelViewMat[1].x, ModelViewMat[2].x);
        vec3 camUp = vec3(ModelViewMat[0].y, ModelViewMat[1].y, ModelViewMat[2].y);
        vec3 camForward = -vec3(ModelViewMat[0].z, ModelViewMat[1].z, ModelViewMat[2].z);
        worldPos = camForward + camRight * (Position.x / ProjMat[0].x) + camUp * (Position.y / ProjMat[1].y);
        gl_Position = vec4(Position.xy, 0.0, 1.0);
    } else {
        vec3 right = cross(vec3(0.0, 1.0, 0.0), forward);
        right = length(right) < 1.0e-4 ? vec3(1.0, 0.0, 0.0) : normalize(right);
        vec3 up = cross(forward, right);

        vec3 semiRight = semi * right;
        vec3 semiUp = semi * up;
        float crossRight = dot(semiRight, semiForward);
        float crossUp = dot(semiUp, semiForward);
        // The fit is exact, so the extreme surface point lands precisely on the quad edge. A hair of slack keeps
        // 32-bit rounding and rasterisation from nicking it there; the density is zero at that boundary anyway,
        // so the extra pixels cost only a ray test that misses.
        float slack = 1.005;
        float halfRight = slack * dist * (abs(crossRight) + sqrt(crossRight * crossRight + outside * dot(semiRight, semiRight))) / outside;
        float halfUp = slack * dist * (abs(crossUp) + sqrt(crossUp * crossUp + outside * dot(semiUp, semiUp))) / outside;

        worldPos = center + right * (Position.x * halfRight) + up * (Position.y * halfUp);
        gl_Position = ProjMat * ModelViewMat * vec4(worldPos, 1.0);
    }

    // Camera-relative position with world axes - the camera sits at the origin, so this doubles as the view ray.
    vPosition = worldPos;

    sphericalVertexDistance = fog_spherical_distance(worldPos);
    cylindricalVertexDistance = fog_cylindrical_distance(worldPos);
}
