package de.teamlapen.lib.client.renderer;

import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.textures.FluidSpriteCache;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class VertexUtils {

    @SuppressWarnings("deprecation")
    public static void renderFluidTank(Level level, BlockPos pos, @Nullable FluidStack fluidStack, int capacity, Vec3 translation, Vec3 scale, PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay) {
        if (fluidStack == null || fluidStack.isEmpty()) return;

        float filled = Mth.clamp((float) fluidStack.getAmount() / capacity, 0f, 1f);

        poseStack.pushPose();

        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.translate(translation);
        poseStack.scale((float) scale.x, (float) scale.y, (float) scale.z);
        poseStack.translate(-0.5, -0.5, -0.5);


        FluidState fluidState = fluidStack.getFluid().defaultFluidState();
        TextureAtlasSprite[] sprites = FluidSpriteCache.getFluidSprites(level, pos, fluidState);
        int tintColor = IClientFluidTypeExtensions.of(fluidState).getTintColor(fluidState, level, pos);
        ChunkSectionLayer renderLayer = ItemBlockRenderTypes.getRenderLayer(fluidState);

        RenderType type = switch (renderLayer) {
            case SOLID -> RenderType.SOLID;
            case TRANSLUCENT -> RenderType.TRANSLUCENT_MOVING_BLOCK;
            case CUTOUT -> RenderType.CUTOUT;
            case TRIPWIRE -> RenderType.TRIPWIRE;
            case CUTOUT_MIPPED -> RenderType.CUTOUT_MIPPED;
        };

        TextureAtlasSprite still = sprites[0];

        float u1 = still.getU(0);
        float u2 = still.getU(1);
        float v1 = still.getV(0);
        float v2 = still.getV(1);

        float height = 1 * filled;


        // West side (x=0)
        nodeCollector.submitCustomGeometry(poseStack, type, (pose, vertexBuilder) -> {
            vertexBuilder.addVertex(pose, 0, 0, 0).setColor(tintColor).setUv(u1, v1).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 1, 0);
            vertexBuilder.addVertex(pose, 0, 0, 1).setColor(tintColor).setUv(u1, v2).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 1, 0);
            vertexBuilder.addVertex(pose, 0, height, 1).setColor(tintColor).setUv(u2, v2).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 1, 0);
            vertexBuilder.addVertex(pose, 0, height, 0).setColor(tintColor).setUv(u2, v1).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 1, 0);
        });

        // East side (x=1)
        nodeCollector.submitCustomGeometry(poseStack, RenderType.translucentMovingBlock(), (pose, vertexBuilder) -> {
            vertexBuilder.addVertex(pose, 1, 0, 1).setColor(tintColor).setUv(u1, v1).setLight(packedLight).setOverlay(packedOverlay).setNormal(1, 0, 0);
            vertexBuilder.addVertex(pose, 1, 0, 0).setColor(tintColor).setUv(u1, v2).setLight(packedLight).setOverlay(packedOverlay).setNormal(1, 0, 0);
            vertexBuilder.addVertex(pose, 1, height, 0).setColor(tintColor).setUv(u2, v2).setLight(packedLight).setOverlay(packedOverlay).setNormal(1, 0, 0);
            vertexBuilder.addVertex(pose, 1, height, 1).setColor(tintColor).setUv(u2, v1).setLight(packedLight).setOverlay(packedOverlay).setNormal(1, 0, 0);
        });

        // North side (z=0)
        nodeCollector.submitCustomGeometry(poseStack, RenderType.translucentMovingBlock(), (pose, vertexBuilder) -> {
            vertexBuilder.addVertex(pose, 1, 0, 0).setColor(tintColor).setUv(u1, v1).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 0, -1);
            vertexBuilder.addVertex(pose, 0, 0, 0).setColor(tintColor).setUv(u1, v2).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 0, -1);
            vertexBuilder.addVertex(pose, 0, height, 0).setColor(tintColor).setUv(u2, v2).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 0, -1);
            vertexBuilder.addVertex(pose, 1, height, 0).setColor(tintColor).setUv(u2, v1).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 0, -1);
        });

        // South side (z=1)
        nodeCollector.submitCustomGeometry(poseStack, RenderType.translucentMovingBlock(), (pose, vertexBuilder) -> {
            vertexBuilder.addVertex(pose, 0, 0, 1).setColor(tintColor).setUv(u1, v1).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 0, 1);
            vertexBuilder.addVertex(pose, 1, 0, 1).setColor(tintColor).setUv(u1, v2).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 0, 1);
            vertexBuilder.addVertex(pose, 1, height, 1).setColor(tintColor).setUv(u2, v2).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 0, 1);
            vertexBuilder.addVertex(pose, 0, height, 1).setColor(tintColor).setUv(u2, v1).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 0, 1);
        });

        // Bottom face (y=0)
        nodeCollector.submitCustomGeometry(poseStack, RenderType.translucentMovingBlock(), (pose, vertexBuilder) -> {
            vertexBuilder.addVertex(pose, 0, 0, 0).setColor(tintColor).setUv(u1, v1).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, -1, 0);
            vertexBuilder.addVertex(pose, 1, 0, 0).setColor(tintColor).setUv(u1, v2).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, -1, 0);
            vertexBuilder.addVertex(pose, 1, 0, 1).setColor(tintColor).setUv(u2, v2).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, -1, 0);
            vertexBuilder.addVertex(pose, 0, 0, 1).setColor(tintColor).setUv(u2, v1).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, -1, 0);
        });

        // Top face (y=1)
        nodeCollector.submitCustomGeometry(poseStack, RenderType.translucentMovingBlock(), (pose, vertexBuilder) -> {
            vertexBuilder.addVertex(pose, 0, height, 1).setColor(tintColor).setUv(u1, v1).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 1, 0);
            vertexBuilder.addVertex(pose, 1, height, 1).setColor(tintColor).setUv(u1, v2).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 1, 0);
            vertexBuilder.addVertex(pose, 1, height, 0).setColor(tintColor).setUv(u2, v2).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 1, 0);
            vertexBuilder.addVertex(pose, 0, height, 0).setColor(tintColor).setUv(u2, v1).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 1, 0);
        });


//
//        var renderType = RenderType.translucentMovingBlock();
//        VertexUtils.addCube(poseStack, nodeCollector, renderType, u1, v2, u2, v2, 1, filled, packedLight, packedOverlay, tintColor, fluidAlpha, true, true, false);
        // The rendered fluid may sometimes appear black because of the packedLight parameter being too dark. Not sure if that's solvable, it occurs on the Minecraft side

        poseStack.popPose();
    }

    public static void addCube(PoseStack poseStack, SubmitNodeCollector nodeCollector, RenderType renderType,
                               float width, float height, int light, int overlay, int color) {
        addCube(poseStack, nodeCollector, renderType, width, height, light, overlay, color, 1f);
    }

    public static void addCube(PoseStack poseStack, SubmitNodeCollector nodeCollector, RenderType renderType,
                               float width, float height, int light, int overlay, int color, float alpha) {
        addCube(poseStack, nodeCollector, renderType, 0, 0, width, height, light, overlay, color, alpha, true, true, false);
    }

    public static void addCube(PoseStack poseStack, SubmitNodeCollector nodeCollector, RenderType renderType,
                               float uOff, float vOff,
                               float width, float height, int light, int overlay, int color) {
        addCube(poseStack, nodeCollector, renderType, uOff, vOff, width, height, light, overlay, color, 1f, true, true, false);
    }

    //automatic relative UV
    //invert v axis automatically
    public static void addCube(PoseStack poseStack, SubmitNodeCollector nodeCollector, RenderType renderType,
                               float uOff, float vOff,
                               float width, float height,
                               int light, int overlay,
                               int color, float alpha,
                               boolean up, boolean down, boolean wrap) {
        addCube(poseStack, nodeCollector, renderType, uOff, 1 - (vOff + height), uOff + width, 1 - vOff,
                width, height, light, overlay, color, alpha, up, down, wrap);
    }

    public static void addCube(PoseStack poseStack, SubmitNodeCollector nodeCollector, RenderType renderType,
                               float minU, float minV,
                               float maxU, float maxV,
                               float width, float height,
                               int light, int overlay,
                               int color, float alpha,
                               boolean up, boolean down, boolean wrap) {
        int lu = light & 0xFFFF;
        int lv = (light >> 16) & 0xFFFF;
        int ou = overlay & 0xFFFF;
        int ov = (overlay >> 16) & 0xFFFF;

        float minV2 = maxV - width;

        int r = ARGB.red(color);
        int g = ARGB.green(color);
        int b = ARGB.blue(color);
        int a = (int) (255 * alpha);

        float hw = width / 2f;
        float hh = height / 2f;

        poseStack.pushPose();
        poseStack.translate(0, hh, 0);
        nodeCollector.submitCustomGeometry(poseStack, renderType, (pose, vertexBuilder) -> {

            float inc = 0;
            for (var d : Direction.values()) {
                float v0 = minV;
                float t = hw;
                float y0 = -hh;
                float y1 = hh;
                float i = inc;
                if (d.getAxis() == Direction.Axis.Y) {
                    if ((!up && d == Direction.UP) || (!down && d == Direction.DOWN)) continue;
                    t = hh;
                    y0 = -hw;
                    y1 = hw;
                    v0 = minV2;
                } else if (wrap) {
                    inc += width;
                }
                poseStack.pushPose();
                poseStack.mulPose(rot(d));
                poseStack.translate(0, 0, -t);
                addQuad(vertexBuilder, poseStack, -hw, y0, hw, y1, minU + i, v0, maxU + i, maxV, r, g, b, a, lu, lv, ou, ov);
                poseStack.popPose();
            }
        });
        poseStack.popPose();
    }

    public static void addQuad(VertexConsumer builder, PoseStack poseStack,
                               float x0, float y0, float x1, float y1, int lu, int lv, int ou, int ov) {
        addQuad(builder, poseStack, x0, y0, x1, y1, 255, 255, 255, 255, lu, lv, ou, ov);
    }

    public static void addQuad(VertexConsumer builder, PoseStack poseStack,
                               float x0, float y0, float x1, float y1,
                               int r, int g, int b, int a,
                               int lu, int lv,
                               int ou, int ov) {
        addQuad(builder, poseStack, x0, y0, x1, y1, 0, 0, 1, 1, r, g, b, a, lu, lv, ou, ov);
    }

    //fast 2d quad. Use matrix to put where you want
    public static void addQuad(VertexConsumer builder, PoseStack poseStack,
                               float x0, float y0, float x1, float y1,
                               float u0, float v0, float u1, float v1,
                               int r, int g, int b, int a,
                               int lu, int lv,
                               int ou, int ov) {
        PoseStack.Pose last = poseStack.last();
        Vector3f vector3f = last.normal().transform(new Vector3f(0, 0, -1));
        float nx = vector3f.x;
        float ny = vector3f.y;
        float nz = vector3f.z;
        //avoids having to multiply 3 times
        vertF(builder, poseStack, x0, y1, 0, u0, v0, r, g, b, a, lu, lv, ou, ov, nx, ny, nz);
        vertF(builder, poseStack, x1, y1, 0, u1, v0, r, g, b, a, lu, lv, ou, ov, nx, ny, nz);
        vertF(builder, poseStack, x1, y0, 0, u1, v1, r, g, b, a, lu, lv, ou, ov, nx, ny, nz);
        vertF(builder, poseStack, x0, y0, 0, u0, v1, r, g, b, a, lu, lv, ou, ov, nx, ny, nz);
    }

    public static void vert(VertexConsumer builder, PoseStack poseStack, float x, float y, float z,
                            float u, float v,
                            float r, float g, float b, float a,
                            int lu, int lv,
                            int ou, int ov,
                            float nx, float ny, float nz) {
        //not chained because of MC263524
        builder.addVertex(poseStack.last().pose(), x, y, z);
        builder.setColor(r, g, b, a);
        builder.setUv(u, v);
        builder.setUv1(ou, ov);
        builder.setUv2(lu, lv);
        builder.setNormal(poseStack.last(), nx, ny, nz);
    }

    private static void vertF(VertexConsumer builder, PoseStack poseStack,
                              float x, float y, float z,
                              float u, float v,
                              int r, int g, int b, int a,
                              int lu, int lv,
                              int ou, int ov,
                              float nx, float ny, float nz) {
        //not chained because of MC263524
        builder.addVertex(poseStack.last().pose(), x, y, z);
        builder.setColor(r, g, b, a);
        builder.setUv(u, v);
        builder.setUv1(ou, ov);
        builder.setUv2(lu, lv);
        builder.setNormal(nx, ny, nz);
    }

    //no normal rotation
    private static void vertF(VertexConsumer builder, PoseStack poseStack,
                              float x, float y, float z,
                              float u, float v,
                              int color,
                              int lu, int lv,
                              int ou, int ov,
                              float nx, float ny, float nz) {
        //not chained because of MC263524
        builder.addVertex(poseStack.last().pose(), x, y, z);
        builder.setColor(color);
        builder.setUv(u, v);
        builder.setUv1(ou, ov);
        builder.setUv2(lu, lv);
        builder.setNormal(nx, ny, nz);
    }

    // got knows why these dont match the ones in LightTexture

    public static int lightU(int light) {
        return light & 0xFFFF;
    }

    public static int lightV(int light) {
        return (light >> 16) & 0xFFFF;
    }

    private static final Direction[] DIRS = new Direction[]{
            Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, null
    };

//    public static List<BakedQuad> getAllModelQuads(BakedModel model, BlockState state, RandomSource rand) {
//        List<BakedQuad> allQuads = new ArrayList<>();
//        for (var d : DIRS) {
//            allQuads.addAll(model.getQuads(state, d, rand));
//        }
//        return allQuads;
//    }

    public static Quaternionf rot(Direction dir) {
        return DIR2ROT.get(dir);
    }

    public static final Quaternionf XN90 = Axis.XP.rotationDegrees(-90);
    private static final Map<Direction, Quaternionf> DIR2ROT = Maps.newEnumMap(Arrays.stream(Direction.values())
            .collect(Collectors.toMap(Functions.identity(), d -> d.getOpposite().getRotation().mul(XN90))));

}
