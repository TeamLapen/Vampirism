package de.teamlapen.vampirism.core;


import de.teamlapen.vampirism.inventory.container.*;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

@ObjectHolder(REFERENCE.MODID)
public class ModContainer {
    public static final ContainerType<HunterTrainerContainer> hunter_trainer = getNull();
    public static final ContainerType<AlchemicalCauldronContainer> alchemical_cauldron = getNull();
    public static final ContainerType<BloodPotionTableContainer> blood_potion_table = getNull();
    public static final ContainerType<HunterBasicContainer> hunter_basic = getNull();
    public static final ContainerType<HunterTableContainer> hunter_table = getNull();
    public static final ContainerType<WeaponTableContainer> weapon_table = getNull();
    public static final ContainerType<AltarInfusionContainer> altar_infusion = getNull();
    public static final ContainerType<BloodGrinderContainer> blood_grinder = getNull();

    public static void registerContainer(IForgeRegistry<ContainerType<?>> registry) {
        registry.register(new ContainerType<HunterTrainerContainer>(HunterTrainerContainer::new).setRegistryName(REFERENCE.MODID, "hunter_trainer"));
        registry.register(new ContainerType<AlchemicalCauldronContainer>(AlchemicalCauldronContainer::new).setRegistryName(REFERENCE.MODID, "alchemical_cauldron"));
        registry.register(new ContainerType<BloodPotionTableContainer>(BloodPotionTableContainer::new).setRegistryName(REFERENCE.MODID, "blood_potion_table"));
        registry.register(new ContainerType<HunterBasicContainer>(HunterBasicContainer::new).setRegistryName(REFERENCE.MODID, "hunter_basic"));
        registry.register(new ContainerType<HunterTableContainer>(HunterTableContainer::new).setRegistryName(REFERENCE.MODID, "hunter_table"));
        registry.register(new ContainerType<WeaponTableContainer>(WeaponTableContainer::new).setRegistryName(REFERENCE.MODID, "weapon_table"));
        registry.register(new ContainerType<AltarInfusionContainer>(AltarInfusionContainer::new).setRegistryName(REFERENCE.MODID, "altar_infusion"));
        registry.register(new ContainerType<BloodGrinderContainer>(BloodGrinderContainer::new).setRegistryName(REFERENCE.MODID, "blood_grinder"));
    }
}
