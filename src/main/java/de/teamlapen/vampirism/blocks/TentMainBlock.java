package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.tileentity.TentTileEntity;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

/**
 * Main block for the 2x2 block tent. Handles spawning
 */
public class TentMainBlock extends TentBlock implements ITileEntityProvider {
    private static final String name = "tent_main";


    public TentMainBlock() {
        super(name);
    }


    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TentTileEntity();
    }

}
