package de.teamlapen.vampirism.client.render.tiles;

import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.vampirism.blocks.VampirismBlockContainer;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * TESR with a few util methods
 */
@OnlyIn(Dist.CLIENT)
abstract class VampirismTESR<T extends TileEntity> extends TileEntityRenderer<T> {


    public VampirismTESR(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    /**
     * Rotates the block to fit the enum facing.
     * ONLY CALL THIS IF THE BLOCK HAS A {@link VampirismBlockContainer#FACING} PROPERTY
     *
     * @param tile
     */
    protected void adjustRotatePivotViaState(@Nullable TileEntity tile) {
        if (tile == null) return;
        Direction dir = Direction.NORTH;
        if (tile.getWorld() != null)
            dir = tile.getWorld().getBlockState(tile.getPos()).get(HorizontalBlock.HORIZONTAL_FACING);
        GlStateManager.rotatef((dir.getHorizontalIndex() - 2) * -90, 0.0F, 1.0F, 0.0F);
    }
}
