package de.teamlapen.vampirism.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Food;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GarlicBreadItem extends VampirismItem {
    private static final String regName = "garlic_bread";

    public GarlicBreadItem() {
        super(regName, new Properties().food((new Food.Builder()).hunger(6).saturation(0.7F).build()).group(ItemGroup.FOOD));
    }


    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        if (!worldIn.isRemote) {
            entityLiving.curePotionEffects(stack);
        }
        return super.onItemUseFinish(stack, worldIn, entityLiving);
    }
}
