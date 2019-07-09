package de.teamlapen.vampirism.items.enchantment;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.items.ItemTechCrossbow;
import de.teamlapen.vampirism.items.VampirismItemCrossbow;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

/**
 * Same as infinite enchantment for bows, but for crossbows
 */
public class EnchantmentCrossbowInfinite extends Enchantment {
    public EnchantmentCrossbowInfinite(Rarity rarityIn) {
        super(rarityIn, VReference.CROSSBOW_ENCHANTMENT, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
        this.setRegistryName(REFERENCE.MODID, "crossbowinfinite");

    }

    @Override
    protected String getDefaultTranslationKey() {
        return "arrow_infinite";
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof VampirismItemCrossbow && !(stack.getItem() instanceof ItemTechCrossbow);
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 20;
    }
}
