package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.items.enchantment.EnchantmentArrowFrugality;
import de.teamlapen.vampirism.items.enchantment.EnchantmentVampireSlayer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;


@ObjectHolder(REFERENCE.MODID)
public class ModEnchantments {

    public static final EnchantmentArrowFrugality crossbowfrugality = getNull();
    public static final EnchantmentVampireSlayer vampireslayer = getNull();


    static void registerEnchantments(IForgeRegistry<Enchantment> registry) {
        //Don't forget to add a enchantment description "enchantment.vampirism.<id>.desc" for new enchantments #624
        registry.register(new EnchantmentArrowFrugality(Enchantment.Rarity.VERY_RARE));
        registry.register(new EnchantmentVampireSlayer(Enchantment.Rarity.UNCOMMON));
    }

    public static void fixMapping(RegistryEvent.MissingMappings<Enchantment> event) {
        event.getAllMappings().forEach(missingMapping -> {
            if ("vampirism:crossbowinfinite".equals(missingMapping.key.toString())) {
                missingMapping.remap(Enchantments.INFINITY);
            }
        });

    }
}
