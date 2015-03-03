package de.teamlapen.vampirism.block;

import java.util.Iterator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * UNUSED, but might get used later
 * @author Max
 *
 */
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
        else{
        	Logger.i("test", "pitch"+player.rotationPitch);
        	player.rotationPitch=-90;
        	((EntityPlayerMP)player).playerNetServerHandler.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, -90);
        	int meta = world.getBlockMetadata(x, y, z);

            if (!isBlockHeadOfBed(meta))
            {
                int j1 = getDirection(meta);
                x += field_149981_a[j1][0];
                z += field_149981_a[j1][1];

                if (world.getBlock(x, y, z) != this)
                {
                    return true;
                }

                meta = world.getBlockMetadata(x, y, z);
            }
            
            //Check if someone else is using this 
            if (isOccupied(meta))
            {
                EntityPlayer entityplayer1 = null;
                Iterator iterator = world.playerEntities.iterator();

                while (iterator.hasNext())
                {
                    EntityPlayer entityplayer2 = (EntityPlayer)iterator.next();

                    if (entityplayer2.isPlayerSleeping())
                    {
                        ChunkCoordinates chunkcoordinates = entityplayer2.playerLocation;

                        if (chunkcoordinates.posX == x && chunkcoordinates.posY == y && chunkcoordinates.posZ == z)
                        {
                            entityplayer1 = entityplayer2;
                        }
                    }
                }

                if (entityplayer1 != null)
                {
                    player.addChatComponentMessage(new ChatComponentTranslation("tile.altar.occupied", new Object[0]));//TODO adjust translation
                    return true;
                }

                func_149979_a(world,x,y,z, false);
            }
            
            Logger.i("test", "starting fake sleep");
            //player.setSize(0.2F, 0.2F);
            player.yOffset = 0.2F;
            int l = getDirection(meta);
            float f1 = 0.5F;
            float f = 0.5F;

                switch (l)
                {
                    case 0:
                        f = 0.9F;
                        break;
                    case 1:
                        f1 = 0.1F;
                        break;
                    case 2:
                        f = 0.1F;
                        break;
                    case 3:
                        f1 = 0.9F;
                }

                //this.func_71013_b(l);
                player.setPosition((double)((float)x + f1), (double)((float)y + 0.9375F), (double)((float)z + f));
            
            S0APacketUseBed s0apacketusebed = new S0APacketUseBed(player, x,y,z);
            ((EntityPlayerMP)player).playerNetServerHandler.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
            ((EntityPlayerMP)player).playerNetServerHandler.sendPacket(s0apacketusebed);
            
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
    
    public static boolean isOccupied(int meta){
    	return func_149976_c(meta);
    }
}
