package de.teamlapen.vampirism.common.world.items.consume;

import de.teamlapen.faction.api.world.items.consume.IFactionFoodBehavior;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.world.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.common.world.entity.vampire.DrinkBloodContext;
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

public class VampireFoodBehaviour implements IFactionFoodBehavior {

    @Override
    public void apply(Level level, LivingEntity livingEntity, ItemStack itemStack, Consumable consumable, FoodProperties foodProperties) {
        RandomSource randomsource = livingEntity.getRandom();
        level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), consumable.sound().value(), SoundSource.NEUTRAL, 1.0F, randomsource.triangle(1.0F, 0.4F));
        if (livingEntity instanceof Player player) {
            FactionPlayerHandler.get(player).factionPlayer(ModFactions.VAMPIRE).ifPresent(vampire -> {
                vampire.drinkBlood(foodProperties.nutrition(), foodProperties.saturation(), new DrinkBloodContext(itemStack));
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5F, Mth.randomBetween(randomsource, 0.9F, 1.0F));
            });
        } else if (livingEntity instanceof VampireMinionEntity minion) {
            minion.eat(foodProperties.nutrition());
        }
    }
}
