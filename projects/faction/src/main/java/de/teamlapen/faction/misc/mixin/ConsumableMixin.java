package de.teamlapen.faction.misc.mixin;

import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.faction.common.world.items.consume.FactionFoodEntry;
import de.teamlapen.faction.common.world.items.consume.FactionFoodList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Consumable.class)
public class ConsumableMixin {

    @Inject(method = "canConsume(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void factions$allowFactionConsume(LivingEntity entity, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        FactionFoodList foodList = stack.get(FactionDataComponents.FACTION_FOOD);
        if (foodList != null && entity instanceof Player player) {
            List<FactionFoodEntry> entries = foodList.findMatchingEntries(entity);

            if (entries.isEmpty()) {
                FoodProperties defaultFood = foodList.defaultFood();
                cir.setReturnValue(player.canEat(defaultFood.canAlwaysEat()) && defaultFood.nutrition() > 0); // If the food properties are empty, then the item shouldn't be edible
                cir.cancel();
            } else {
                boolean canEat = false;

                for (FactionFoodEntry entry : entries) {
                    if (player.canEat(entry.foodProperties().canAlwaysEat())) {
                        canEat = true;
                    }
                }

                cir.setReturnValue(canEat);
                cir.cancel();
            }
        }
    }
}
