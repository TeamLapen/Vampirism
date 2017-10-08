package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.tileentity.TileTent;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Main block for the 2x2 block tent. Handles spawning
 */
public class BlockTentMain extends BlockTent implements ITileEntityProvider {
    private static final String name = "tent_main";

    public BlockTentMain() {
        super(name);
        hasTileEntity = true;
        this.setTickRandomly(true);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileTent tile = (TileTent) worldIn.getTileEntity(pos);
        if (tile != null && tile.isSpawner()) {
            ItemStack stack;
            switch (worldIn.rand.nextInt(4)) {
                case 0:
                    stack = new ItemStack(Items.BREAD, 1);
                    break;
                case 1:
                    stack = new ItemStack(Items.COAL, 2);
                    break;
                case 2:
                    stack = new ItemStack(Blocks.PLANKS, 1);
                    break;
                default:
                    stack = new ItemStack(Items.APPLE, 1);
            }
            worldIn.spawnEntity(new EntityItem(worldIn, pos.getX(), pos.getY() + 0.3, pos.getZ(), stack));
        }
        super.breakBlock(worldIn, pos, state);
        worldIn.spawnEntity(new EntityItem(worldIn, pos.getX(), pos.getY() + 1, pos.getZ(), new ItemStack(ModItems.item_tent, 1)));

    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileTent();
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
        //Fix related to #210
        //Tents with no tileentity set, create a new one and enable spawn
        //TODO remove sometime
        if (worldIn.getChunkFromBlockCoords(pos).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) == null) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileTent) {
                ((TileTent) tile).setSpawn(true);
            }
        }
    }
}
