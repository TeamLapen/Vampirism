package de.teamlapen.vampirism.common.world.items.consume;

import de.teamlapen.faction.common.world.items.consume.FactionFoodProperties;
import de.teamlapen.vampirism.common.core.ModFactions;
import net.minecraft.world.food.FoodProperties;

public class ModFoods {

    // TODO: Make it somehow possible to have two different faction foods on one item.
    public static final FactionFoodProperties GARLIC_BREAD_HUNTER = new FactionFoodProperties.Builder(ModFactions.HUNTER).nutrition(8).saturationModifier(0.8F).build();
    public static final FoodProperties GARLIC_BREAD_HUMAN = new FoodProperties.Builder().nutrition(6).saturationModifier(0.7F).build();
    public static final VampireFoodProperties HUMAN_HEART_VAMPIRE = new VampireFoodProperties.Builder().blood(20).saturationModifier(1.5F).build();
    public static final FoodProperties HUMAN_HEART_HUMAN = new FoodProperties.Builder().nutrition(5).saturationModifier(1f).build();
    public static final VampireFoodProperties WEAK_HUMAN_HEART_VAMPIRE = new VampireFoodProperties.Builder().blood(10).saturationModifier(0.9F).build();
    public static final FoodProperties WEAK_HUMAN_HEART_HUMAN = new FoodProperties.Builder().nutrition(3).saturationModifier(1f).build();
}
