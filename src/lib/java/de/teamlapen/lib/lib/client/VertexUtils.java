package de.teamlapen.lib.lib.client;

import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VertexUtils {

    public static void renderFluidTank(Holder<Fluid> fluid, @Nullable Integer fluidAmount, int capacity, Vec3 translation, Vec3 scale, float fluidAlpha, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        renderFluidTank(new FluidStack(fluid, fluidAmount == null ? 0 : fluidAmount), capacity, translation, scale, fluidAlpha, poseStack, bufferSource, packedLight, packedOverlay);
    }

    @SuppressWarnings("deprecation")
    public static void renderFluidTank(@Nullable FluidStack fluidStack, int capacity, Vec3 translation, Vec3 scale, float fluidAlpha, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (fluidStack != null && !fluidStack.isEmpty()) {
            float filled = Mth.clamp((float) fluidStack.getAmount() / capacity, 0f, 1f);
            IClientFluidTypeExtensions fluidExtension = IClientFluidTypeExtensions.of(fluidStack.getFluid());
            Material material = new Material(TextureAtlas.LOCATION_BLOCKS, fluidExtension.getStillTexture(fluidStack));

            poseStack.pushPose();

            poseStack.translate(translation);
            poseStack.scale((float) scale.x, (float) scale.y, (float) scale.z);

            VertexConsumer vertex = material.buffer(bufferSource, RenderType::entityTranslucent);
            VertexUtils.addCube(vertex, poseStack, 1, filled, packedLight, packedOverlay, -1, fluidAlpha);

            poseStack.popPose();
        }
    }

    public static void addCube(VertexConsumer builder, PoseStack poseStack,
                               float width, float height, int light, int overlay, int color) {
        addCube(builder, poseStack, width, height, light, overlay, color, 1f);
    }

    public static void addCube(VertexConsumer builder, PoseStack poseStack,
                               float width, float height, int light, int overlay, int color, float alpha) {
        addCube(builder, poseStack, 0, 0, width, height, light, overlay, color, alpha, true, true, false);
    }

    public static void addCube(VertexConsumer builder, PoseStack poseStack,
                               float uOff, float vOff,
                               float width, float height, int light, int overlay, int color) {
        addCube(builder, poseStack, uOff, vOff, width, height, light, overlay, color, 1f, true, true, false);
    }

    //automatic relative UV
    //invert v axis automatically
    public static void addCube(VertexConsumer builder, PoseStack poseStack,
                               float uOff, float vOff,
                               float width, float height,
                               int light, int overlay,
                               int color, float alpha,
                               boolean up, boolean down, boolean wrap) {
        addCube(builder, poseStack, uOff, 1 - (vOff + height), uOff + width, 1 - vOff,
                width, height, light, overlay, color, alpha, up, down, wrap);
    }

    public static void addCube(VertexConsumer builder, PoseStack poseStack,
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
        float inc = 0;

        poseStack.pushPose();
        poseStack.translate(0, hh, 0);
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
            addQuad(builder, poseStack, -hw, y0, hw, y1, minU + i, v0, maxU + i, maxV, r, g, b, a, lu, lv, ou, ov);
            poseStack.popPose();
        }
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

    public static List<BakedQuad> getAllModelQuads(BakedModel model, BlockState state, RandomSource rand) {
        List<BakedQuad> allQuads = new ArrayList<>();
        for (var d : DIRS) {
            allQuads.addAll(model.getQuads(state, d, rand));
        }
        return allQuads;
    }

    public static Quaternionf rot(Direction dir) {
        return DIR2ROT.get(dir);
    }

    public static final Quaternionf XN90 = Axis.XP.rotationDegrees(-90);
    private static final Map<Direction, Quaternionf> DIR2ROT = Maps.newEnumMap(Arrays.stream(Direction.values())
            .collect(Collectors.toMap(Functions.identity(), d -> d.getOpposite().getRotation().mul(XN90))));


}
