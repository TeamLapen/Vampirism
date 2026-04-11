package de.teamlapen.faction.misc.mixin;

import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.faction.common.world.items.consume.FactionFoodEntry;
import de.teamlapen.faction.common.world.items.consume.FactionFoodList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import squeek.appleskin.helpers.ConsumableFood;
import squeek.appleskin.helpers.FoodHelper;

import java.util.List;

@Mixin(FoodHelper.class)
public class FoodHelperMixin {

    @Inject(method = "isFood", at = @At("RETURN"), cancellable = true)
    private static void considerFactionFood(ItemStack itemStack, Player player, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() || itemStack.has(FactionDataComponents.FACTION_FOOD));
    }

    @Inject(method = "getDefaultFoodValues", at = @At("RETURN"), cancellable = true, remap = false)
    private static void injectFactionFoodValues(ItemStack itemStack, Player player, CallbackInfoReturnable<ConsumableFood> cir) {
        if (itemStack.has(FactionDataComponents.FACTION_FOOD)) {
            FactionFoodList foodList = itemStack.get(FactionDataComponents.FACTION_FOOD);
            if (foodList == null) return;

            List<FactionFoodEntry> entries = foodList.findMatchingEntries(player);
            if (entries.isEmpty()) {
                cir.setReturnValue(new ConsumableFood(foodList.defaultFood(), itemStack.getOrDefault(DataComponents.CONSUMABLE, FoodHelper.DEFAULT_CONSUMABLE)));
                return;
            }

            int totalNutrition = 0;
            float totalSaturationValue = 0;
            for (FactionFoodEntry entry : entries) {
                FoodProperties properties = entry.foodProperties();
                totalNutrition += properties.nutrition();
                totalSaturationValue += properties.saturation();
            }
            float predictiveSaturation = totalSaturationValue / (totalNutrition * 2F);

            FoodProperties predictiveProperties = new FoodProperties.Builder().nutrition(totalNutrition).saturationModifier(predictiveSaturation).build();

            Consumable consumable = itemStack.getOrDefault(DataComponents.CONSUMABLE, FoodHelper.DEFAULT_CONSUMABLE);
            cir.setReturnValue(new ConsumableFood(predictiveProperties, consumable));
        }
    }
}
