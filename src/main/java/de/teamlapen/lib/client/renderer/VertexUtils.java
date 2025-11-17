package de.teamlapen.lib.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.textures.FluidSpriteCache;
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

        poseStack.popPose();
    }

}
