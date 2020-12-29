package de.teamlapen.vampirism.client.render.tiles;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.client.model.CoffinModel;
import de.teamlapen.vampirism.tileentity.CoffinTileEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Render the coffin with it's different colors and the lid opening animation
 */
@OnlyIn(Dist.CLIENT)
public class CoffinTESR extends VampirismTESR<CoffinTileEntity> {
    private final int maxLidPos = 61;
    private final CoffinModel model;
    private final ResourceLocation[] textures = new ResourceLocation[DyeColor.values().length];
    private final Logger LOGGER = LogManager.getLogger();

    public CoffinTESR(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
        this.model = new CoffinModel();
        for (DyeColor e : DyeColor.values()) {
            textures[e.getId()] = new ResourceLocation(REFERENCE.MODID, "textures/block/coffin/coffin_" + e.getString() + ".png");
        }
    }

    @Override
    public void render(CoffinTileEntity tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {
        if (!tile.renderAsItem) {
            if (!isHeadSafe(tile.getWorld(), tile.getPos())) return;

            // Calculate lid position
            boolean occupied = tile.hasWorld() && CoffinBlock.isOccupied(tile.getWorld(), tile.getPos());
            if (!occupied && tile.lidPos > 0)
                tile.lidPos--;
            else if (occupied && tile.lidPos < maxLidPos)
                tile.lidPos++;
        } else {
            tile.lidPos = maxLidPos;
        }
        model.rotateLid(calcLidAngle(tile.lidPos));
        int color = Math.min(tile.color.getId(), 15);
        matrixStack.push();
        matrixStack.translate(0.5F, +1.5F, 0.5F);
        matrixStack.push();
        adjustRotatePivotViaState(tile, matrixStack);
        matrixStack.rotate(Vector3f.XP.rotationDegrees(180));
        matrixStack.translate(0, 0, -1);
        IVertexBuilder vertexBuilder = iRenderTypeBuffer.getBuffer(RenderType.getEntitySolid(textures[color]));
        this.model.render(matrixStack, vertexBuilder, i, i1, 1, 1, 1, 1);
        matrixStack.pop();
        matrixStack.pop();

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
    private boolean isHeadSafe(World world, BlockPos pos) {
        try {
            return CoffinBlock.isHead(world, pos);
        } catch (IllegalArgumentException e) {
            LOGGER.error("CoffinTESR", "Failed to check coffin head at %s caused by wrong blockstate. Block at that pos: %s", pos, world.getBlockState(pos));
        } catch (Exception e) {
            LOGGER.error("CoffinTESR", e, "Failed to check coffin head at %s.", pos);
        }
        return false;
    }
}
