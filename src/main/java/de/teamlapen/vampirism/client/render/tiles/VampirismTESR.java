package de.teamlapen.vampirism.client.render.tiles;

import de.teamlapen.vampirism.blocks.VampirismBlockContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * TESR with a few util methods
 */
@SideOnly(Side.CLIENT)
public abstract class VampirismTESR<T extends TileEntity> extends TileEntitySpecialRenderer<T> {

    /**
     * Rotates the block to fit the enum facing.
     * ONLY CALL THIS IF THE BLOCK HAS A {@link VampirismBlockContainer#FACING} PROPERTY
     *
     * @param tile
     */
    protected void adjustRotatePivotViaState(@Nullable TileEntity tile) {
        if (tile == null) return;
        EnumFacing dir = EnumFacing.NORTH;
        if (tile.getWorld() != null)
            dir = tile.getWorld().getBlockState(tile.getPos()).getValue(VampirismBlockContainer.FACING);
        GlStateManager.rotate((dir.getHorizontalIndex() - 2) * -90, 0.0F, 1.0F, 0.0F);
    }
}
