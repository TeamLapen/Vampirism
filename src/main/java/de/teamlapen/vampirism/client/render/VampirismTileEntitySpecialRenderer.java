package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.block.BasicBlockContainer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.eclipse.jdt.annotation.Nullable;
import org.lwjgl.opengl.GL11;

/**
 * Created by Max on 07.09.2015.
 */
public abstract class VampirismTileEntitySpecialRenderer extends TileEntitySpecialRenderer {

    protected void adjustRotatePivotViaMeta(@Nullable TileEntity tile) {
        if(tile==null)return;
        EnumFacing dir=EnumFacing.NORTH;
        if (tile.getWorld() != null)
            dir = BasicBlockContainer.getFacing(tile.getWorld().getBlockState(tile.getPos()));
        GL11.glRotatef(dir.getIndex() - 2 * 90, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public void renderTileEntityAt(TileEntity p_180535_1_, double posX, double posZ, double p_180535_6_, float p_180535_8_, int p_180535_9_) {
        renderTileEntity(p_180535_1_,posX,posZ,p_180535_6_,p_180535_8_,p_180535_9_);
    }

    public abstract void renderTileEntity(TileEntity te, double x, double y, double z,float p5, int p6);
}
