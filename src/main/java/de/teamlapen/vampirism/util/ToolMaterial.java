package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.api.items.IItemWithTier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.SimpleTier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ToolMaterial {

    public static class Tiered extends SimpleTier {

        private final IItemWithTier.TIER tier;

        public Tiered(IItemWithTier.TIER tier, TagKey<Block> incorrect, int uses, float speed, float damage, int enchantmentValue, Supplier<Ingredient> repairIngredient) {
            super(incorrect, uses, speed, damage, enchantmentValue, repairIngredient);
            this.tier = tier;
        }

        public IItemWithTier.TIER getTier() {
            return tier;
        }
    }
}
