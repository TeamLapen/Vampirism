package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.entity.EntityGarlicBomb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by Max on 23.10.2015.
 */
public class ItemGarlicBomb extends BasicItem {
    public static final String name = "garlic_bomb";

    public ItemGarlicBomb() {
        super(name);
        this.setMaxStackSize(4);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!player.capabilities.isCreativeMode) {
            --stack.stackSize;
        }

        world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!world.isRemote) {
            world.spawnEntityInWorld(new EntityGarlicBomb(world, player));
        }

        return stack;
    }
}
