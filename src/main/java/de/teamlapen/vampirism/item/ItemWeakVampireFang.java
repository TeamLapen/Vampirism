package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.Configs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemWeakVampireFang extends ItemVampireFang {
    public static final String name = "weakVampireFang";

    public ItemWeakVampireFang() {
        super(name);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (Configs.realismMode) return stack;//Only strong fangs can turn
        return super.onItemRightClick(stack, world, player);
    }
}
