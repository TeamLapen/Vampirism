package de.teamlapen.vampirism.client.render.tiles;

import com.google.common.collect.Streams;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.tileentity.GarlicBeaconTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class GarlicBeaconTESR extends VampirismTESR<GarlicBeaconTileEntity> {
    public GarlicBeaconTESR(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(GarlicBeaconTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferInOld, int combinedLightIn, int combinedOverlayIn) {
        Entity e = Minecraft.getInstance().getCameraEntity();
        if (e != null && Streams.stream(e.getHandSlots()).map(ItemStack::getItem).anyMatch(i -> i == ModItems.GARLIC_FINDER.get()) && tileEntityIn.isInRange(e.blockPosition())) {
            long totalWorldTime = tileEntityIn.getLevel() != null ? tileEntityIn.getLevel().getGameTime() : 0;
            float scale = (float) MathHelper.clamp(Math.sqrt(tileEntityIn.getBlockPos().distSqr(e.blockPosition())) / 16, 1, 3);
            IVertexBuilder bufferIn = bufferInOld.getBuffer(Accessor.CUTOUT_NODEPTH);

            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5D, 0.5D, 0.5D);
            matrixStackIn.scale(scale, scale, scale);

            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees((totalWorldTime + partialTicks) % 360));
            matrixStackIn.translate(-0.5D, 0, -0.5);
            matrixStackIn.pushPose();

            //Matrix stack is already translated to block pos
//        GarlicBeaconBlock.shape.forEachEdge((p_230013_12_, p_230013_14_, p_230013_16_, p_230013_18_, p_230013_20_, p_230013_22_) -> {
//            bufferIn.pos(matrix4f, (float)(p_230013_12_ ), (float)(p_230013_14_ ), (float)(p_230013_16_ )).color(1, 1, 1, 1f).endVertex();
//            bufferIn.pos(matrix4f, (float)(p_230013_18_ ), (float)(p_230013_20_ ), (float)(p_230013_22_ )).color(1, 1, 1, 1f).endVertex();
//        });


            Minecraft.getInstance().getItemRenderer().renderModelLists(Minecraft.getInstance().getItemRenderer().getModel(new ItemStack(ModItems.ITEM_GARLIC.get()), null, null), new ItemStack(ModItems.ITEM_GARLIC.get()), combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
            matrixStackIn.popPose();
            matrixStackIn.popPose();
        }
    }

    @Override
    public boolean shouldRenderOffScreen(GarlicBeaconTileEntity te) {
        return true;
    }

    private static class Accessor extends RenderState {
        private static final RenderType CUTOUT_NODEPTH = RenderType.create("cutout_nodepth", DefaultVertexFormats.BLOCK, 7, 131072, true, false, RenderType.State.builder().setDepthTestState(NO_DEPTH_TEST).setTextureState(BLOCK_SHEET).setAlphaState(MIDWAY_ALPHA).setFogState(NO_FOG).createCompositeState(true));

        public Accessor(String nameIn, Runnable setupTaskIn, Runnable clearTaskIn) {
            super(nameIn, setupTaskIn, clearTaskIn);
        }
    }
}
