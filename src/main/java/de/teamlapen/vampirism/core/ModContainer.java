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
    public static final ContainerType<HunterBasicContainer> hunter_basic = getNull();
    public static final ContainerType<HunterTableContainer> hunter_table = getNull();
    public static final ContainerType<WeaponTableContainer> weapon_table = getNull();
    public static final ContainerType<AltarInfusionContainer> altar_infusion = getNull();
    public static final ContainerType<BloodGrinderContainer> blood_grinder = getNull();
    public static final ContainerType<MinionContainer> minion = getNull();
    public static final ContainerType<TaskBoardContainer> task_master = getNull();
    public static final ContainerType<PotionTableContainer> extended_potion_table = getNull();


    static void registerContainer(IForgeRegistry<ContainerType<?>> registry) {
        registry.register(new ContainerType<>(HunterTrainerContainer::new).setRegistryName(REFERENCE.MODID, "hunter_trainer"));
        registry.register(new ContainerType<>(AlchemicalCauldronContainer::new).setRegistryName(REFERENCE.MODID, "alchemical_cauldron"));
        registry.register(new ContainerType<>(HunterBasicContainer::new).setRegistryName(REFERENCE.MODID, "hunter_basic"));
        registry.register(new ContainerType<>(HunterTableContainer::new).setRegistryName(REFERENCE.MODID, "hunter_table"));
        registry.register(new ContainerType<>(new WeaponTableContainer.Factory()).setRegistryName(REFERENCE.MODID, "weapon_table"));
        registry.register(new ContainerType<>(AltarInfusionContainer::new).setRegistryName(REFERENCE.MODID, "altar_infusion"));
        registry.register(new ContainerType<>(BloodGrinderContainer::new).setRegistryName(REFERENCE.MODID, "blood_grinder"));
        registry.register(new ContainerType<>(new MinionContainer.Factory()).setRegistryName(REFERENCE.MODID, "minion"));
        registry.register(new ContainerType<>(TaskBoardContainer::new).setRegistryName(REFERENCE.MODID, "task_master"));
        registry.register(new ContainerType<>(new PotionTableContainer.Factory()).setRegistryName(REFERENCE.MODID, "extended_potion_table"));
    }
}
