package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.items.enchantment.ArrowFrugalityEnchantment;
import de.teamlapen.vampirism.items.enchantment.VampireSlayerEnchantment;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(Registries.ENCHANTMENT, REFERENCE.MODID);

    public static final DeferredHolder<Enchantment, ArrowFrugalityEnchantment> ARROW_FRUGALITY = ENCHANTMENTS.register("arrow_frugality", ArrowFrugalityEnchantment::new);
    public static final DeferredHolder<Enchantment, DamageEnchantment> VAMPIRE_SLAYER = ENCHANTMENTS.register("vampire_slayer", VampireSlayerEnchantment::new);

    static void register(IEventBus bus) {
        ENCHANTMENTS.register(bus);
    }
}
