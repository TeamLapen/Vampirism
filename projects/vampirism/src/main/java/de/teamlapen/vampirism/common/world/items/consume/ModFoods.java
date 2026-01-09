package de.teamlapen.vampirism.common.world.items.consume;

import de.teamlapen.faction.common.world.items.consume.FactionFoodEntry;
import de.teamlapen.faction.common.world.items.consume.FactionFoodList;
import de.teamlapen.vampirism.common.core.ModFoodBehaviours;
import de.teamlapen.vampirism.common.tags.ModFactionTags;
import net.minecraft.world.food.FoodProperties;

public class ModFoods {

    public static final FoodProperties GARLIC_BREAD_HUMAN = new FoodProperties.Builder().nutrition(6).saturationModifier(0.7F).build();
    public static final FoodProperties GARLIC_BREAD_HUNTER = new FoodProperties.Builder().nutrition(8).saturationModifier(0.8F).build();
    public static final FoodProperties HUMAN_HEART_HUMAN = new FoodProperties.Builder().nutrition(5).saturationModifier(1f).build();
    public static final FoodProperties HUMAN_HEART_VAMPIRE = new FoodProperties.Builder().nutrition(20).saturationModifier(1.5F).build();
    public static final FoodProperties WEAK_HUMAN_HEART_HUMAN = new FoodProperties.Builder().nutrition(3).saturationModifier(1f).build();
    public static final FoodProperties WEAK_HUMAN_HEART_VAMPIRE = new FoodProperties.Builder().nutrition(10).saturationModifier(0.9F).build();

    public static final FactionFoodList GARLIC_BREAD = new FactionFoodList(GARLIC_BREAD_HUMAN, hunterFood(GARLIC_BREAD_HUNTER));
    public static final FactionFoodList HUMAN_HEART = new FactionFoodList(HUMAN_HEART_HUMAN, vampireFood(HUMAN_HEART_VAMPIRE));
    public static final FactionFoodList WEAK_HUMAN_HEART = new FactionFoodList(WEAK_HUMAN_HEART_HUMAN, vampireFood(WEAK_HUMAN_HEART_VAMPIRE));

    public static FactionFoodEntry vampireFood(FoodProperties foodProperties) {
        return new FactionFoodEntry(ModFactionTags.IS_VAMPIRE, foodProperties, ModFoodBehaviours.VAMPIRE_FOOD.getId());
    }

    public static FactionFoodEntry hunterFood(FoodProperties foodProperties) {
        return new FactionFoodEntry(ModFactionTags.IS_HUNTER, foodProperties);
    }
}
