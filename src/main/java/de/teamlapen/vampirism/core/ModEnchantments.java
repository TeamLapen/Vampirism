package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.items.enchantment.EnchantmentArrowFrugality;
import de.teamlapen.vampirism.items.enchantment.EnchantmentCrossbowInfinite;
import de.teamlapen.vampirism.items.enchantment.EnchantmentVampireSlayer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;


@GameRegistry.ObjectHolder(REFERENCE.MODID)
public class ModEnchantments {

    public static final EnchantmentCrossbowInfinite crossbowinfinite = getNull();
    public static final EnchantmentArrowFrugality crossbowfrugality = getNull();
    public static final EnchantmentVampireSlayer vampireslayer = getNull();

    @SuppressWarnings("ConstantConditions")
    private static @Nonnull
    <T> T getNull() {
        return null;
    }

    static void registerEnchantments(IForgeRegistry<Enchantment> registry) {
        registry.register(new EnchantmentCrossbowInfinite(Enchantment.Rarity.VERY_RARE));
        registry.register(new EnchantmentArrowFrugality(Enchantment.Rarity.VERY_RARE));
        registry.register(new EnchantmentVampireSlayer(Enchantment.Rarity.UNCOMMON));
    }

}
