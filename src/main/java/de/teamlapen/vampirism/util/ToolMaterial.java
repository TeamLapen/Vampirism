package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.api.items.IItemWithTier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.function.Supplier;

public class ToolMaterial {

    public static class Tiered extends SimpleTier {

        private final IItemWithTier.Tier tier;

        public Tiered(IItemWithTier.Tier tier, TagKey<Block> incorrect, int uses, float speed, float damage, int enchantmentValue, Supplier<Ingredient> repairIngredient) {
            super(incorrect, uses, speed, damage, enchantmentValue, repairIngredient);
            this.tier = tier;
        }

        public IItemWithTier.Tier getTier() {
            return tier;
        }
    }
}
