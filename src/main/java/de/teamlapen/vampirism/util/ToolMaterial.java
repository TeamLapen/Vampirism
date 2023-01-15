package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.api.items.IItemWithTier;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ToolMaterial implements Tier {

    private final int uses;
    private final float speed;
    private final float attackDamageBonus;
    private final int level;
    private final int enchantmentValue;
    private final Supplier<Ingredient> repairIngredient;

    public ToolMaterial(int level, int uses, float speed, float damage, int enchantmentValue, Supplier<Ingredient> repairIngredient) {
        this.uses = uses;
        this.speed = speed;
        this.attackDamageBonus = damage;
        this.level = level;
        this.enchantmentValue = enchantmentValue;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public float getAttackDamageBonus() {
        return attackDamageBonus;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public @NotNull Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int getUses() {
        return uses;
    }

    public static class Tiered extends ToolMaterial {

        private final IItemWithTier.TIER tier;

        public Tiered(IItemWithTier.TIER tier, int level, int uses, float speed, float damage, int enchantmentValue, Supplier<Ingredient> repairIngredient) {
            super(level, uses, speed, damage, enchantmentValue, repairIngredient);
            this.tier = tier;
        }

        public IItemWithTier.TIER getTier() {
            return tier;
        }
    }
}
