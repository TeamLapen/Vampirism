package de.teamlapen.vampirism.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Max on 17.10.2015.
 */
public class ItemGarlic extends BasicItem implements IPlantable {

    public static final String name = "item_garlic";
    private Block plant;

    public ItemGarlic(Block garlic) {
        super("garlic");
        plant = garlic;
    }

    @Override
    public boolean onItemUse(ItemStack p_77648_1_, EntityPlayer player, World world, int posX, int posY, int posZ, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_) {
        if (p_77648_7_ != 1) {
            return false;
        } else if (player.canPlayerEdit(posX, posY, posZ, p_77648_7_, p_77648_1_) && player.canPlayerEdit(posX, posY + 1, posZ, p_77648_7_, p_77648_1_)) {
            if (world.getBlock(posX, posY, posZ).canSustainPlant(world, posX, posY, posZ, ForgeDirection.UP, this) && world.isAirBlock(posX, posY + 1, posZ)) {
                world.setBlock(posX, posY + 1, posZ, this.plant);
                --p_77648_1_.stackSize;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
        return EnumPlantType.Crop;
    }

    @Override
    public Block getPlant(IBlockAccess world, int x, int y, int z) {
        return plant;
    }

    @Override
    public int getPlantMetadata(IBlockAccess world, int x, int y, int z) {
        return 0;
    }
}
