package de.teamlapen.vampirism.client.renderer.blockentity;

import com.google.common.collect.Streams;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;


public class GarlicDiffuserBESR extends VampirismBESR<de.teamlapen.vampirism.blockentity.diffuser.GarlicDiffuserBlockEntity> {
    public GarlicDiffuserBESR(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(@NotNull de.teamlapen.vampirism.blockentity.diffuser.GarlicDiffuserBlockEntity tileEntityIn, float partialTicks, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        Entity e = Minecraft.getInstance().getCameraEntity();
        if (e != null && Streams.stream(e.getHandSlots()).map(ItemStack::getItem).anyMatch(i -> i == ModItems.GARLIC_FINDER.get()) && tileEntityIn.isInRange(e.blockPosition())) {
            long totalWorldTime = tileEntityIn.getLevel() != null ? tileEntityIn.getLevel().getGameTime() : 0;
            float scale = (float) Mth.clamp(Math.sqrt(tileEntityIn.getBlockPos().distSqr(e.blockPosition())) / 16, 1, 3);
            VertexConsumer vertexConsumer = bufferIn.getBuffer(Accessor.CUTOUT_NODEPTH);

            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5D, 0.5D, 0.5D);
            matrixStackIn.scale(scale, scale, scale);

            matrixStackIn.mulPose(Axis.YP.rotationDegrees((totalWorldTime + partialTicks) % 360));
            matrixStackIn.translate(-0.5D, 0, -0.5);
            matrixStackIn.pushPose();

            BakedModel garlic_model = Minecraft.getInstance().getItemRenderer().getModel(new ItemStack(ModItems.ITEM_GARLIC.get()), null, null, 0);
            Minecraft.getInstance().getItemRenderer().renderModelLists(garlic_model, new ItemStack(ModItems.ITEM_GARLIC.get()), combinedLightIn, combinedOverlayIn, matrixStackIn, vertexConsumer);
            matrixStackIn.popPose();
            matrixStackIn.popPose();
        }
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull de.teamlapen.vampirism.blockentity.diffuser.GarlicDiffuserBlockEntity te) {
        return true;
    }

    private static class Accessor extends RenderStateShard {
        private static final RenderType CUTOUT_NODEPTH = RenderType.create("cutout_nodepth", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 131072, true, false, RenderType.CompositeState.builder().setLightmapState(RenderStateShard.LIGHTMAP).setDepthTestState(NO_DEPTH_TEST).setTextureState(BLOCK_SHEET).setShaderState(RenderStateShard.RENDERTYPE_CUTOUT_SHADER).createCompositeState(true));

        public Accessor(@NotNull String nameIn, @NotNull Runnable setupTaskIn, @NotNull Runnable clearTaskIn) {
            super(nameIn, setupTaskIn, clearTaskIn);
        }
    }
}
