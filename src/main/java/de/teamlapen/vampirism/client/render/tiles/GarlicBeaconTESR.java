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
    public boolean isGlobalRenderer(GarlicBeaconTileEntity te) {
        return true;
    }

    @Override
    public void render(GarlicBeaconTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferInOld, int combinedLightIn, int combinedOverlayIn) {
        Entity e = Minecraft.getInstance().getRenderViewEntity();
        if (e != null && Streams.stream(e.getHeldEquipment()).map(ItemStack::getItem).anyMatch(i -> i == ModItems.garlic_finder) && tileEntityIn.isInRange(e.getPosition())) {
            long totalWorldTime = tileEntityIn.getWorld() != null ? tileEntityIn.getWorld().getGameTime() : 0;
            float scale = (float) MathHelper.clamp(Math.sqrt(tileEntityIn.getPos().distanceSq(e.getPosition())) / 16, 1, 3);
            IVertexBuilder bufferIn = bufferInOld.getBuffer(Accessor.CUTOUT_NODEPTH);

            matrixStackIn.push();
            matrixStackIn.translate(0.5D, 0.5D, 0.5D);
            matrixStackIn.scale(scale, scale, scale);

            matrixStackIn.rotate(Vector3f.YP.rotationDegrees((totalWorldTime + partialTicks) % 360));
            matrixStackIn.translate(-0.5D, 0, -0.5);
            matrixStackIn.push();

            //Matrix stack is already translated to block pos
//        GarlicBeaconBlock.shape.forEachEdge((p_230013_12_, p_230013_14_, p_230013_16_, p_230013_18_, p_230013_20_, p_230013_22_) -> {
//            bufferIn.pos(matrix4f, (float)(p_230013_12_ ), (float)(p_230013_14_ ), (float)(p_230013_16_ )).color(1, 1, 1, 1f).endVertex();
//            bufferIn.pos(matrix4f, (float)(p_230013_18_ ), (float)(p_230013_20_ ), (float)(p_230013_22_ )).color(1, 1, 1, 1f).endVertex();
//        });


            Minecraft.getInstance().getItemRenderer().renderModel(Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(new ItemStack(ModItems.item_garlic), null, null), new ItemStack(ModItems.item_garlic), combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
            matrixStackIn.pop();
            matrixStackIn.pop();
        }
    }

    private static class Accessor extends RenderState {
        private static final RenderType CUTOUT_NODEPTH = RenderType.makeType("cutout_nodepth", DefaultVertexFormats.BLOCK, 7, 131072, true, false, RenderType.State.getBuilder().depthTest(DEPTH_ALWAYS).texture(BLOCK_SHEET).alpha(HALF_ALPHA).fog(NO_FOG).build(true));

        public Accessor(String nameIn, Runnable setupTaskIn, Runnable clearTaskIn) {
            super(nameIn, setupTaskIn, clearTaskIn);
        }
    }
}
