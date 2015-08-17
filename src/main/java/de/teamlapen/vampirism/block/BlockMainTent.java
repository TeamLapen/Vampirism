package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.tileEntity.TileEntityTent;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Main tent block. Holds the tent's tile entity
 */
public class BlockMainTent extends BlockTent implements ITileEntityProvider {

    public static final String name = "tent_main";

    public BlockMainTent() {
        super();
        isBlockContainer = true;
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEntityTent();
    }

    @Override
    public int getRenderType() {
        return -1;
    }


    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int meta) {
        super.breakBlock(world, x, y, z, p_149749_5_, meta);
        world.spawnEntityInWorld(new EntityItem(world, x, y + 1, z, new ItemStack(ModItems.tent, 1)));
    }

    public boolean onBlockEventReceived(World p_149696_1_, int p_149696_2_, int p_149696_3_, int p_149696_4_, int p_149696_5_, int p_149696_6_) {
        super.onBlockEventReceived(p_149696_1_, p_149696_2_, p_149696_3_, p_149696_4_, p_149696_5_, p_149696_6_);
        TileEntity tileentity = p_149696_1_.getTileEntity(p_149696_2_, p_149696_3_, p_149696_4_);
        return tileentity != null ? tileentity.receiveClientEvent(p_149696_5_, p_149696_6_) : false;
    }

    @Override
    public boolean onBlockActivated(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        ((TileEntityTent) p_149727_1_.getTileEntity(p_149727_2_, p_149727_3_, p_149727_4_)).onActivated(p_149727_5_);
        return true;
    }
}
