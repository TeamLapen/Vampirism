package de.teamlapen.faction.client.integration;

import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.faction.common.world.items.consume.FactionFoodEntry;
import de.teamlapen.faction.common.world.items.consume.FactionFoodList;
import de.teamlapen.integration.Integration;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import squeek.appleskin.api.event.FoodValuesEvent;

import java.util.List;

@Integration(modId = "appleskin")
public class AppleSkinIntegration {

    @SubscribeEvent
    public static void applyFactionFoodValues(FoodValuesEvent event) {
        ItemStack itemStack = event.itemStack;

        if (itemStack.has(FactionDataComponents.FACTION_FOOD)) {
            FactionFoodList foodList = itemStack.get(FactionDataComponents.FACTION_FOOD);
            if (foodList == null) return;

            List<FactionFoodEntry> entries = foodList.findMatchingEntries(event.player);
            if (entries.isEmpty()) {
                event.defaultFoodProperties = event.modifiedFoodProperties = foodList.defaultFood();
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

            event.defaultFoodProperties = event.modifiedFoodProperties = predictiveProperties;
        }
    }
}
