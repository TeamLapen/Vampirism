package de.teamlapen.vampirism.client.renderer.blockentity;

import com.google.common.collect.Streams;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.common.blockentity.diffuser.GarlicDiffuserBlockEntity;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class GarlicDiffuserRenderer implements BlockEntityRenderer<GarlicDiffuserBlockEntity> {

    private final ItemStackRenderState garlicRenderState;

    public GarlicDiffuserRenderer(BlockEntityRendererProvider.Context context) {
        this.garlicRenderState = new ItemStackRenderState();
        context.getItemModelResolver().updateForTopItem(garlicRenderState, ModBlocks.GARLIC.toStack(), ItemDisplayContext.GROUND, false, null, null, 16);
    }

    @Override
    public void render(GarlicDiffuserBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (Minecraft.getInstance().getCameraEntity() instanceof LivingEntity living && Streams.stream(living.getHandSlots()).map(ItemStack::getItem).anyMatch(i -> i == ModItems.GARLIC_FINDER.get()) && blockEntity.isInRange(living.blockPosition())) {
            long totalWorldTime = blockEntity.getLevel() != null ? blockEntity.getLevel().getGameTime() : 0;
            float scale = (float) Mth.clamp(Math.sqrt(blockEntity.getBlockPos().distSqr(living.blockPosition())) / 16, 1, 3);
            VertexConsumer vertexConsumer = bufferSource.getBuffer(Accessor.CUTOUT_NODEPTH);

            poseStack.pushPose();
            poseStack.translate(0.5D, 0.5D, 0.5D);
            poseStack.scale(scale, scale, scale);

            poseStack.mulPose(Axis.YP.rotationDegrees((totalWorldTime + partialTick) % 360));
            poseStack.translate(-0.5D, 0, -0.5);
            poseStack.pushPose();

            this.garlicRenderState.render(poseStack, renderType -> vertexConsumer, packedLight, packedOverlay);
            poseStack.popPose();
            poseStack.popPose();
        }
    }

    @Override
    public boolean shouldRenderOffScreen(GarlicDiffuserBlockEntity blockEntity) {
        return true;
    }

    private static class Accessor extends RenderStateShard {
        private static final RenderType CUTOUT_NODEPTH = RenderType.create("cutout_nodepth", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 131072, true, false, RenderType.CompositeState.builder().setLightmapState(RenderStateShard.LIGHTMAP).setDepthTestState(NO_DEPTH_TEST).setTextureState(BLOCK_SHEET).setShaderState(RenderStateShard.RENDERTYPE_CUTOUT_SHADER).createCompositeState(true));

        public Accessor(String nameIn, Runnable setupTaskIn, Runnable clearTaskIn) {
            super(nameIn, setupTaskIn, clearTaskIn);
        }
    }
}
