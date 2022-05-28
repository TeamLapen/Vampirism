package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.tileentity.TentTileEntity;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Main block for the 2x2 block tent. Handles spawning
 */
public class TentMainBlock extends TentBlock implements ITileEntityProvider {

    public TentMainBlock() {
        super();
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(@Nonnull IBlockReader worldIn) {
        return new TentTileEntity();
    }

}
