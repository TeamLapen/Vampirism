package de.teamlapen.faction.api.world.items.consume;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;

@FunctionalInterface
public interface IFactionFoodBehavior {

    void apply(Level level, LivingEntity livingEntity, ItemStack itemStack, Consumable consumable, FoodProperties foodProperties);
}
