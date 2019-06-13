package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.items.enchantment.EnchantmentArrowFrugality;
import de.teamlapen.vampirism.items.enchantment.EnchantmentCrossbowInfinite;
import de.teamlapen.vampirism.items.enchantment.EnchantmentVampireSlayer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;


@ObjectHolder(REFERENCE.MODID)
public class ModEnchantments {

    public static final EnchantmentCrossbowInfinite crossbowinfinite = getNull();
    public static final EnchantmentArrowFrugality crossbowfrugality = getNull();
    public static final EnchantmentVampireSlayer vampireslayer = getNull();


    static void registerEnchantments(IForgeRegistry<Enchantment> registry) {
        registry.register(new EnchantmentCrossbowInfinite(Enchantment.Rarity.VERY_RARE));
        registry.register(new EnchantmentArrowFrugality(Enchantment.Rarity.VERY_RARE));
        registry.register(new EnchantmentVampireSlayer(Enchantment.Rarity.UNCOMMON));
    }

}
