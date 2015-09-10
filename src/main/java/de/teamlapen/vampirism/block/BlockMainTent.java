package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.tileEntity.TileEntityTent;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * Main tent block. Holds the tent's tile entity
 */
public class BlockMainTent extends BlockTent implements ITileEntityProvider {

    @Override
    public String[] getVariantsToRegister() {
        return new String[]{name};
    }

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
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        worldIn.spawnEntityInWorld(new EntityItem(worldIn,pos.getX(),pos.getY()+1,pos.getZ(),new ItemStack(ModItems.tent,1)));
    }



    @Override
    public boolean onBlockEventReceived(World worldIn, BlockPos pos, IBlockState state, int eventID, int eventParam) {
         super.onBlockEventReceived(worldIn, pos, state, eventID, eventParam);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity != null ? tileentity.receiveClientEvent(pos.getX(),pos.getY()) : false;
    }


    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        ((TileEntityTent) worldIn.getTileEntity(pos)).onActivated(playerIn);
        return true;
    }
}
