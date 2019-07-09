package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.tileentity.TileTent;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Main block for the 2x2 block tent. Handles spawning
 */
public class BlockTentMain extends BlockTent implements ITileEntityProvider {
    private static final String name = "tent_main";

    public BlockTentMain() {
        super(name);
    }


    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileTent();
    }

    public void getDrops(BlockState state, NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune) {
        TileTent tile = (TileTent) world.getTileEntity(pos);
        if (tile != null && tile.isSpawner()) {
            ItemStack stack;
            switch (world.rand.nextInt(4)) {
                case 0:
                    stack = new ItemStack(Items.BREAD, 1);
                    break;
                case 1:
                    stack = new ItemStack(Items.COAL, 2);
                    break;
                case 2:
                    stack = new ItemStack(Blocks.OAK_PLANKS, 1);
                    break;
                default:
                    stack = new ItemStack(Items.APPLE, 1);
            }
            drops.add(stack);
        }
        drops.add(new ItemStack(ModItems.item_tent));

    }

    //TODO 1.13 check if tents still work after reloading the world
}
