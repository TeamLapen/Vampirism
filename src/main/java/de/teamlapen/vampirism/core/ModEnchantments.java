package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.items.enchantment.EnchantmentArrowFrugality;
import de.teamlapen.vampirism.items.enchantment.EnchantmentCrossbowInfinite;
import de.teamlapen.vampirism.items.enchantment.EnchantmentVampireSlayer;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.common.registry.GameRegistry;


public class ModEnchantments {

    public static EnchantmentCrossbowInfinite crossbowInfinite;
    public static EnchantmentArrowFrugality crossbowFrugality;
    public static EnchantmentVampireSlayer vampireSlayer;


    static void registerEnchantments() {
        crossbowInfinite = register(new EnchantmentCrossbowInfinite(Enchantment.Rarity.VERY_RARE));
        crossbowFrugality = register(new EnchantmentArrowFrugality(Enchantment.Rarity.VERY_RARE));
        vampireSlayer = register(new EnchantmentVampireSlayer(Enchantment.Rarity.UNCOMMON));
    }

    private static <T extends Enchantment> T register(T enchantment) {
        GameRegistry.register(enchantment);
        return enchantment;
    }
}
