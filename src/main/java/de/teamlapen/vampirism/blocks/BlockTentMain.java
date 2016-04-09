package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.tileentity.TileTent;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Main block for the 2x2 block tent. Handles spawning
 */
public class BlockTentMain extends BlockTent implements ITileEntityProvider {
    private static final String name = "tentMain";

    public BlockTentMain() {
        super(name);
        isBlockContainer = true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        worldIn.spawnEntityInWorld(new EntityItem(worldIn, pos.getX(), pos.getY() + 1, pos.getZ(), new ItemStack(ModItems.itemTent, 1)));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileTent();
    }

    @Override
    public boolean onBlockEventReceived(World worldIn, BlockPos pos, IBlockState state, int eventID, int eventParam) {
        TileEntity tile = worldIn.getTileEntity(pos);
        return tile != null && tile.receiveClientEvent(eventID, eventParam);
    }
}
