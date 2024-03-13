package de.teamlapen.vampirism.items.enchantment;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.items.ICrossbow;
import de.teamlapen.vampirism.api.items.IVampirismCrossbow;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;

/**
 * Makes tech crossbows use less arrows
 */
public class EnchantmentArrowFrugality extends Enchantment {
    public EnchantmentArrowFrugality(@NotNull Rarity rarityIn) {
        super(rarityIn, VReference.CROSSBOW_ENCHANTMENT, new EquipmentSlot[]{EquipmentSlot.MAINHAND});

    }

    @Override
    public boolean canApplyAtEnchantingTable(@NotNull ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ICrossbow;
    }

    @Override
    public int getMaxCost(int enchantmentLevel) {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public int getMinCost(int enchantmentLevel) {
        return 10 + enchantmentLevel * 5;
    }

    @Override
    protected boolean checkCompatibility(@NotNull Enchantment ench) {
        return super.checkCompatibility(ench) && ench != Enchantments.INFINITY_ARROWS;
    }

    @NotNull
    @Override
    protected String getOrCreateDescriptionId() {
        return "enchantment.vampirism.arrow_frugality";
    }
}
