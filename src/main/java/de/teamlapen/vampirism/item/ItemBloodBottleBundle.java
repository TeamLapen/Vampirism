package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Bundle of blood bottles only used to store them more efficiently
 */
public class ItemBloodBottleBundle extends BasicItem {

    public final static String name = "blood_bottle_bundle";

    public ItemBloodBottleBundle() {
        super(name);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack p_77659_1_, World p_77659_2_, EntityPlayer player) {
        for (int i = 0; i < 4; i++) {
            ItemStack stack = new ItemStack(ModItems.bloodBottle, 1, ItemBloodBottle.MAX_BLOOD);
            player.inventory.addItemStackToInventory(stack);
            if (stack.stackSize > 0 && !p_77659_2_.isRemote) {
                player.entityDropItem(stack, 0.5F);
            }
        }
        p_77659_1_.stackSize--;
        return p_77659_1_;
    }
}
