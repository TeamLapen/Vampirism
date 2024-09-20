package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class GarlicBreadItem extends Item {

    public GarlicBreadItem() {
        super(new Properties().food((new FoodProperties.Builder()).nutrition(6).saturationModifier(0.7F).build()));
    }


    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean held) {
        Helper.handleHeldNonVampireItem(stack, entity, held);
    }

    @NotNull
    @Override
    public ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull LivingEntity entityLiving) {
        if (!worldIn.isClientSide) {
            entityLiving.removeEffectsCuredBy(ModItems.GARLIC_CURE);
        }
        return super.finishUsingItem(stack, worldIn, entityLiving);
    }
}
