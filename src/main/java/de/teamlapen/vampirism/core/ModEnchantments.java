package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.items.enchantment.EnchantmentArrowFrugality;
import de.teamlapen.vampirism.items.enchantment.EnchantmentVampireSlayer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, REFERENCE.MODID);

    public static final RegistryObject<EnchantmentArrowFrugality> crossbowfrugality =
            ENCHANTMENTS.register("crossbowfrugality", () -> new EnchantmentArrowFrugality(Enchantment.Rarity.VERY_RARE));
    public static final RegistryObject<EnchantmentVampireSlayer> vampireslayer =
            ENCHANTMENTS.register("vampireslayer", () -> new EnchantmentVampireSlayer(Enchantment.Rarity.UNCOMMON));


    static void registerEnchantments(IEventBus bus) {
        ENCHANTMENTS.register(bus);
    }

    static void fixMapping(RegistryEvent.MissingMappings<Enchantment> event) {
        event.getAllMappings().forEach(missingMapping -> {
            if ("vampirism:crossbowinfinite".equals(missingMapping.key.toString())) {
                missingMapping.remap(Enchantments.INFINITY_ARROWS);
            }
        });
    }
}
