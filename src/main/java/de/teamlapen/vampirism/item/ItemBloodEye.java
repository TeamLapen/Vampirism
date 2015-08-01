package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.generation.castle.CastlePositionData;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 * Temp item class to find castles
 */
public class ItemBloodEye extends BasicItem {
    public final static String name="bloodEye";
    public ItemBloodEye() {
        super(name);
    }

    public ItemStack onItemRightClick(ItemStack stack, World p_77659_2_, EntityPlayer player) {
        MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(p_77659_2_, player, false);

            if (!p_77659_2_.isRemote){
                CastlePositionData.Position pos=CastlePositionData.get(p_77659_2_).findNearestCastle(MathHelper.floor_double(player.posX),MathHelper.floor_double(player.posZ));
                if(pos==null){
                    Logger.w("BloodEye","Cannot find nearest castle");
                    return stack;
                }
                else{
                    EntityEnderEye eye=new EntityEnderEye(p_77659_2_,player.posX, player.posY + 1.62D - (double)player.yOffset, player.posZ);
                    eye.moveTowards(pos.chunkXPos<<4,(int)player.posY+5,pos.chunkZPos<<4);
                    p_77659_2_.spawnEntityInWorld(eye);
                }
                if(!player.capabilities.isCreativeMode){
                    stack.stackSize--;
                }
            }

        return stack;
    }
}
