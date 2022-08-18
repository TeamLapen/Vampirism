package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.items.enchantment.EnchantmentArrowFrugality;
import de.teamlapen.vampirism.items.enchantment.EnchantmentVampireSlayer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;


public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, REFERENCE.MODID);

    public static final RegistryObject<EnchantmentArrowFrugality> CROSSBOWFRUGALITY = ENCHANTMENTS.register("crossbowfrugality", () -> new EnchantmentArrowFrugality(Enchantment.Rarity.VERY_RARE));
    public static final RegistryObject<EnchantmentVampireSlayer> VAMPIRESLAYER = ENCHANTMENTS.register("vampireslayer", () -> new EnchantmentVampireSlayer(Enchantment.Rarity.UNCOMMON));


    static void register(IEventBus bus) {
        ENCHANTMENTS.register(bus);
    }

    static void fixMapping(@NotNull MissingMappingsEvent event) {
        event.getAllMappings(ForgeRegistries.Keys.ENCHANTMENTS).forEach(missingMapping -> {
            //noinspection SwitchStatementWithTooFewBranches
            switch (missingMapping.getKey().toString()) {
                case "vampirism:crossbowinfinite" -> missingMapping.remap(Enchantments.INFINITY_ARROWS);
            }
        });
    }
}
