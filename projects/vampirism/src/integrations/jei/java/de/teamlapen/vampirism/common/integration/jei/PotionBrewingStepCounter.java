package de.teamlapen.vampirism.common.integration.jei;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

import java.util.*;

public class PotionBrewingStepCounter {

    public static final PotionBrewingStepCounter INSTANCE = new PotionBrewingStepCounter();

    private final Map<String, Integer> stepCache = new HashMap<>();
    private final Map<String, Set<String>> inputMap = new HashMap<>();

    private PotionBrewingStepCounter() {
        stepCache.put(getUid(PotionContents.createItemStack(Items.POTION, Potions.WATER)), 0);
    }

    public void addVanillaRecipe(List<ItemStack> inputs, ItemStack output) {
        String outputUid = getUid(output);
        for (ItemStack input : inputs) {
            inputMap.computeIfAbsent(outputUid, k -> new HashSet<>()).add(getUid(input));
        }
        stepCache.clear();
        stepCache.put(getUid(PotionContents.createItemStack(Items.POTION, Potions.WATER)), 0);
    }

    public void addVaporStillRecipe(ItemStack input, ItemStack output) {
        addVanillaRecipe(List.of(input), output);
    }

    public int getBrewingSteps(ItemStack output) {
        return resolve(getUid(output), new HashSet<>());
    }

    private int resolve(String uid, Set<String> visited) {
        Integer cached = stepCache.get(uid);
        if (cached != null) return cached;
        if (!visited.add(uid)) return Integer.MAX_VALUE;

        Set<String> inputs = inputMap.getOrDefault(uid, Set.of());
        int min = inputs.stream().mapToInt(input -> resolve(input, visited)).min().orElse(Integer.MAX_VALUE);

        int steps = min == Integer.MAX_VALUE ? Integer.MAX_VALUE : min + 1;
        stepCache.put(uid, steps);
        return steps;
    }

    private static String getUid(ItemStack stack) {
        var contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents != null) {
            String itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
            String potionId = contents.potion().map(holder -> holder.unwrapKey().map(key -> key.identifier().toString()).orElse("unknown")).orElse("none");
            return itemId + "/" + potionId;
        }
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }
}
