package de.teamlapen.vampirism.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBloodAltarTier4Bed extends BlockBed {

	public final static String name="bloodAltarTier4Bed";
	
	public BlockBloodAltarTier4Bed(){
		this.setBlockTextureName("bed");//TODO change
	}
	
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        if (world.isRemote)
        {
            return true;
        }
        return true;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side,int meta){
    	if(side==0){
    		return Blocks.obsidian.getBlockTextureFromSide(side);
    	}
    	return super.getIcon(side, meta);
    }
}
