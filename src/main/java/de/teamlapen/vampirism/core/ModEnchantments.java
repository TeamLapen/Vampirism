package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.items.enchantment.EnchantmentArrowFrugality;
import de.teamlapen.vampirism.items.enchantment.EnchantmentCrossbowInfinite;
import de.teamlapen.vampirism.items.enchantment.EnchantmentVampireSlayer;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;


public class ModEnchantments {

    public static EnchantmentCrossbowInfinite crossbowInfinite;
    public static EnchantmentArrowFrugality crossbowFrugality;
    public static EnchantmentVampireSlayer vampireSlayer;

    public static void onInitStep(IInitListener.Step step, FMLStateEvent event) {
        switch (step) {
            case INIT:
                init((FMLInitializationEvent) event);
                break;
            default://Do nothing
        }

    }

    private static void init(FMLInitializationEvent event) {
        crossbowInfinite = register(new EnchantmentCrossbowInfinite(Enchantment.Rarity.VERY_RARE));
        crossbowFrugality = register(new EnchantmentArrowFrugality(Enchantment.Rarity.VERY_RARE));
        vampireSlayer = register(new EnchantmentVampireSlayer(Enchantment.Rarity.UNCOMMON));
    }

    private static <T extends Enchantment> T register(T enchantment) {
        GameRegistry.register(enchantment);
        return enchantment;
    }
}
