package de.teamlapen.vampirism.client.render.tiles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.blockentity.CoffinBlockEntity;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.model.CoffinModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

/**
 * Render the coffin with its different colors and the lid opening animation
 */
@OnlyIn(Dist.CLIENT)
public class CoffinBESR extends VampirismBESR<CoffinBlockEntity> {
    private final int maxLidPos = 61;
    private final CoffinModel model;
    private final ResourceLocation[] textures = new ResourceLocation[DyeColor.values().length];
    private final Logger LOGGER = LogManager.getLogger();

    public CoffinBESR(BlockEntityRendererProvider.Context context) {
        this.model = new CoffinModel(context.bakeLayer(ModEntitiesRender.COFFIN));
        for (DyeColor e : DyeColor.values()) {
            textures[e.getId()] = new ResourceLocation(REFERENCE.MODID, "textures/block/coffin/coffin_" + e.getSerializedName() + ".png");
        }
    }

    @Override
    public void render(CoffinBlockEntity tile, float partialTicks, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource iRenderTypeBuffer, int i, int i1) {
        if (!tile.renderAsItem) {
            if (!isHeadSafe(tile.getLevel(), tile.getBlockPos())) return;

            // Calculate lid position
            boolean occupied = tile.hasLevel() && CoffinBlock.isClosed(tile.getLevel(), tile.getBlockPos());
            if (!occupied && tile.lidPos > 0)
                tile.lidPos--;
            else if (occupied && tile.lidPos < maxLidPos)
                tile.lidPos++;
        } else {
            tile.lidPos = maxLidPos;
        }
        model.rotateLid(calcLidAngle(tile.lidPos));
        int color = Math.min(tile.color.getId(), 15);
        matrixStack.pushPose();
        matrixStack.translate(0.5F, +1.5F, 0.5F);
        matrixStack.pushPose();
        adjustRotatePivotViaState(tile, matrixStack);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(180));
        matrixStack.translate(0, 0, -1);
        VertexConsumer vertexBuilder = iRenderTypeBuffer.getBuffer(RenderType.entitySolid(textures[color]));
        this.model.renderToBuffer(matrixStack, vertexBuilder, i, i1, 1, 1, 1, 1);
        matrixStack.popPose();
        matrixStack.popPose();

    }


    private float calcLidAngle(int pos) {
        if (pos == maxLidPos)
            return 0.0F;
        else if (pos == 0)
            return (float) (0.75F * Math.PI);
        return (float) (-Math.pow(1.02, pos) + 1 + 0.75 * Math.PI);
    }

    /**
     * Checks if the coffin part at the given pos is the head of the coffin. Any exception is caught and false is returned
     */
    private boolean isHeadSafe(Level world, BlockPos pos) {
        try {
            return CoffinBlock.isHead(world, pos);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Failed to check coffin head at {} caused by wrong blockstate. Block at that pos: {}", pos, world.getBlockState(pos));
        } catch (Exception e) {
            LOGGER.error("Failed to check coffin head.", e);
        }
        return false;
    }
}
