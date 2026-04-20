package de.teamlapen.vampirism.common.world.items.recipes;

import com.google.common.collect.Lists;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.core.ModRecipes;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.common.world.items.SerumInjectionItem;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.*;

public abstract class SerumFromPotionRecipe extends CustomWeaponTableRecipe {

    protected final int requiredPotionCount;

    public SerumFromPotionRecipe(int requiredPotionCount) {
        this.requiredPotionCount = requiredPotionCount;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int potionCount = 0;
        boolean hasSyringe = false;
        List<PotionContents> found = new ArrayList<>();

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.is(Items.POTION)) {
                PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
                if (contents.potion().isPresent() && SerumInjectionItem.isBlockedPotion(contents.potion().get())) {
                    return false;
                }
                found.add(contents);
                potionCount++;
            } else if (stack.is(ModItems.SYRINGE_EMPTY.get())) {
                hasSyringe = true;
            } else {
                return false;
            }
        }

        if (potionCount != requiredPotionCount || !hasSyringe) return false;

        if (found.size() > 1) {
            List<ArrayList<MobEffectInstance>> allEffects = found.stream()
                    .map(c -> Lists.newArrayList(c.getAllEffects()))
                    .toList();
            boolean allSingle = allEffects.stream().allMatch(e -> e.size() == 1);
            if (allSingle) {
                Holder<MobEffect> first = allEffects.getFirst().getFirst().getEffect();
                boolean allSameEffect = allEffects.stream().allMatch(e -> e.getFirst().getEffect().is(first));
                return !allSameEffect;
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        List<PotionContents> potionContents = new ArrayList<>();

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.is(Items.POTION)) {
                potionContents.add(stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY));
            }
        }

        if (potionContents.isEmpty()) return ItemStack.EMPTY;

        Map<Holder<MobEffect>, MobEffectInstance> strongest = new LinkedHashMap<>();
        for (PotionContents contents : potionContents) {
            for (MobEffectInstance effect : contents.getAllEffects()) {
                strongest.merge(
                        effect.getEffect(),
                        new MobEffectInstance(effect),
                        (existing, incoming) -> incoming.getAmplifier() > existing.getAmplifier() ? incoming : existing
                );
            }
        }

        PotionContents merged = potionContents.getFirst();
        List<MobEffectInstance> baseEffects = Lists.newArrayList(potionContents.getFirst().getAllEffects());

        for (Map.Entry<Holder<MobEffect>, MobEffectInstance> entry : strongest.entrySet()) {
            boolean isFromBase = baseEffects.stream().anyMatch(effect -> effect.getEffect() == entry.getKey());
            if (!isFromBase) {
                merged = merged.withEffectAdded(entry.getValue());
            } else if (entry.getValue().getAmplifier() > baseEffects.stream()
                    .filter(effect -> effect.getEffect() == entry.getKey())
                    .findFirst().map(MobEffectInstance::getAmplifier).orElse(0)) {
                merged = new PotionContents(Optional.empty(), merged.customColor(), merged.customEffects(), merged.customName());
                merged = merged.withEffectAdded(entry.getValue());
            }
        }

        ItemStack result = ModItems.SERUM_INJECTION.get().getDefaultInstance();
        result.set(DataComponents.POTION_CONTENTS, merged);
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);

        for (int i = 0; i < input.size(); i++) {
            if (input.getItem(i).is(Items.POTION)) {
                remaining.set(i, new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        return remaining;
    }

    @Override
    public int getRequiredLavaUnits() {
        return 0;
    }

    @Override
    public int getRequiredLevel() {
        return 1;
    }

    @Override
    public List<Ingredient> getIngredients() {
        List<Ingredient> potions = Collections.nCopies(requiredPotionCount, Ingredient.of(Items.POTION));
        List<Ingredient> result = new ArrayList<>(potions);
        result.add(Ingredient.of(ModItems.SYRINGE_EMPTY.get()));
        return result;
    }

    public static class SingleSerumFromPotionRecipe extends SerumFromPotionRecipe {

        public SingleSerumFromPotionRecipe() {
            super(1);
        }

        @Override
        public List<Holder<ISkill<?>>> getRequiredSkills() {
            return List.of();
        }

        @Override
        public RecipeSerializer<SingleSerumFromPotionRecipe> getSerializer() {
            return ModRecipes.SINGLE_SERUM_FROM_POTION_RECIPE.get();
        }
    }

    public static class DoubleSerumFromPotionRecipe extends SerumFromPotionRecipe {

        public DoubleSerumFromPotionRecipe() {
            super(2);
        }

        @Override
        public List<Holder<ISkill<?>>> getRequiredSkills() {
            return List.of(HunterSkills.BIVALENT_INJECTIONS);
        }

        @Override
        public RecipeSerializer<DoubleSerumFromPotionRecipe> getSerializer() {
            return ModRecipes.DOUBLE_SERUM_FROM_POTION_RECIPE.get();
        }
    }
}
