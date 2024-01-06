package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.items.enchantment.EnchantmentArrowFrugality;
import de.teamlapen.vampirism.items.enchantment.EnchantmentVampireSlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(Registries.ENCHANTMENT, REFERENCE.MODID);

    public static final DeferredHolder<Enchantment, EnchantmentArrowFrugality> CROSSBOWFRUGALITY = ENCHANTMENTS.register("crossbowfrugality", () -> new EnchantmentArrowFrugality(Enchantment.Rarity.VERY_RARE));
    public static final DeferredHolder<Enchantment, EnchantmentVampireSlayer> VAMPIRESLAYER = ENCHANTMENTS.register("vampireslayer", () -> new EnchantmentVampireSlayer(Enchantment.Rarity.UNCOMMON));


    static void register(IEventBus bus) {
        ENCHANTMENTS.register(bus);
    }
}
