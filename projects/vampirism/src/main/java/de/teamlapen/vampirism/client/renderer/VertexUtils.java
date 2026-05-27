package de.teamlapen.vampirism.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class VertexUtils {

    public static void renderFluidTank(Level level, BlockPos pos, @Nullable FluidStack fluidStack, int capacity, Vec3 translation, Vec3 scale, PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay) {
        if (fluidStack == null || fluidStack.isEmpty()) return;

        float filled = Mth.clamp((float) fluidStack.getAmount() / capacity, 0f, 1f);

        poseStack.pushPose();

        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.translate(translation);
        poseStack.scale((float) scale.x, (float) scale.y, (float) scale.z);
        poseStack.translate(-0.5, -0.5, -0.5);


        FluidState fluidState = fluidStack.getFluid().defaultFluidState();

        FluidModel fluidModel = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluidState);
        ChunkSectionLayer renderLayer = fluidModel.layer();
        int tintColor = fluidModel.fluidTintSource() != null ? fluidModel.fluidTintSource().color(fluidState) : -1;
        TextureAtlasSprite still = fluidModel.stillMaterial().sprite();

        RenderType type = switch (renderLayer) {
            case SOLID -> RenderTypes.solidMovingBlock();
            case TRANSLUCENT -> RenderTypes.translucentMovingBlock();
            case CUTOUT -> RenderTypes.cutoutMovingBlock();
        };


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
        nodeCollector.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(), (pose, vertexBuilder) -> {
            vertexBuilder.addVertex(pose, 1, 0, 1).setColor(tintColor).setUv(u1, v1).setLight(packedLight).setOverlay(packedOverlay).setNormal(1, 0, 0);
            vertexBuilder.addVertex(pose, 1, 0, 0).setColor(tintColor).setUv(u1, v2).setLight(packedLight).setOverlay(packedOverlay).setNormal(1, 0, 0);
            vertexBuilder.addVertex(pose, 1, height, 0).setColor(tintColor).setUv(u2, v2).setLight(packedLight).setOverlay(packedOverlay).setNormal(1, 0, 0);
            vertexBuilder.addVertex(pose, 1, height, 1).setColor(tintColor).setUv(u2, v1).setLight(packedLight).setOverlay(packedOverlay).setNormal(1, 0, 0);
        });

        // North side (z=0)
        nodeCollector.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(), (pose, vertexBuilder) -> {
            vertexBuilder.addVertex(pose, 1, 0, 0).setColor(tintColor).setUv(u1, v1).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 0, -1);
            vertexBuilder.addVertex(pose, 0, 0, 0).setColor(tintColor).setUv(u1, v2).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 0, -1);
            vertexBuilder.addVertex(pose, 0, height, 0).setColor(tintColor).setUv(u2, v2).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 0, -1);
            vertexBuilder.addVertex(pose, 1, height, 0).setColor(tintColor).setUv(u2, v1).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 0, -1);
        });

        // South side (z=1)
        nodeCollector.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(), (pose, vertexBuilder) -> {
            vertexBuilder.addVertex(pose, 0, 0, 1).setColor(tintColor).setUv(u1, v1).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 0, 1);
            vertexBuilder.addVertex(pose, 1, 0, 1).setColor(tintColor).setUv(u1, v2).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 0, 1);
            vertexBuilder.addVertex(pose, 1, height, 1).setColor(tintColor).setUv(u2, v2).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 0, 1);
            vertexBuilder.addVertex(pose, 0, height, 1).setColor(tintColor).setUv(u2, v1).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 0, 1);
        });

        // Bottom face (y=0)
        nodeCollector.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(), (pose, vertexBuilder) -> {
            vertexBuilder.addVertex(pose, 0, 0, 0).setColor(tintColor).setUv(u1, v1).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, -1, 0);
            vertexBuilder.addVertex(pose, 1, 0, 0).setColor(tintColor).setUv(u1, v2).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, -1, 0);
            vertexBuilder.addVertex(pose, 1, 0, 1).setColor(tintColor).setUv(u2, v2).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, -1, 0);
            vertexBuilder.addVertex(pose, 0, 0, 1).setColor(tintColor).setUv(u2, v1).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, -1, 0);
        });

        // Top face (y=1)
        nodeCollector.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(), (pose, vertexBuilder) -> {
            vertexBuilder.addVertex(pose, 0, height, 1).setColor(tintColor).setUv(u1, v1).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 1, 0);
            vertexBuilder.addVertex(pose, 1, height, 1).setColor(tintColor).setUv(u1, v2).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 1, 0);
            vertexBuilder.addVertex(pose, 1, height, 0).setColor(tintColor).setUv(u2, v2).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 1, 0);
            vertexBuilder.addVertex(pose, 0, height, 0).setColor(tintColor).setUv(u2, v1).setLight(packedLight).setOverlay(packedOverlay).setNormal(0, 1, 0);
        });

        poseStack.popPose();
    }

}
