package de.teamlapen.faction.common.world.items.consume;

import de.teamlapen.faction.api.world.items.consume.IFactionFoodBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;

public class DefaultFactionFoodBehaviour implements IFactionFoodBehavior {

    @Override
    public void apply(Level level, LivingEntity livingEntity, ItemStack itemStack, Consumable consumable, FoodProperties foodProperties) {
        RandomSource randomsource = livingEntity.getRandom();
        level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), consumable.sound().value(), SoundSource.NEUTRAL, 1.0F, randomsource.triangle(1.0F, 0.4F));
        if (livingEntity instanceof Player player) {
            player.getFoodData().eat(foodProperties.nutrition(), foodProperties.saturation());
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5F, Mth.randomBetween(randomsource, 0.9F, 1.0F));
        }
    }
}
