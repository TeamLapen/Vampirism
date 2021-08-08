package de.teamlapen.vampirism.core;


import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.inventory.container.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

@ObjectHolder(REFERENCE.MODID)
public class ModContainer {
    public static final MenuType<HunterTrainerContainer> hunter_trainer = getNull();
    public static final MenuType<AlchemicalCauldronContainer> alchemical_cauldron = getNull();
    public static final MenuType<HunterBasicContainer> hunter_basic = getNull();
    public static final MenuType<HunterTableContainer> hunter_table = getNull();
    public static final MenuType<WeaponTableContainer> weapon_table = getNull();
    public static final MenuType<AltarInfusionContainer> altar_infusion = getNull();
    public static final MenuType<BloodGrinderContainer> blood_grinder = getNull();
    public static final MenuType<MinionContainer> minion = getNull();
    public static final MenuType<TaskBoardContainer> task_master = getNull();
    public static final MenuType<PotionTableContainer> extended_potion_table = getNull();
    public static final MenuType<VampirismContainer> vampirism = getNull();


    static void registerContainer(IForgeRegistry<MenuType<?>> registry) {
        registry.register(new MenuType<>(HunterTrainerContainer::new).setRegistryName(REFERENCE.MODID, "hunter_trainer"));
        registry.register(new MenuType<>(AlchemicalCauldronContainer::new).setRegistryName(REFERENCE.MODID, "alchemical_cauldron"));
        registry.register(new MenuType<>(HunterBasicContainer::new).setRegistryName(REFERENCE.MODID, "hunter_basic"));
        registry.register(new MenuType<>(new HunterTableContainer.Factory()).setRegistryName(REFERENCE.MODID, "hunter_table"));
        registry.register(new MenuType<>(new WeaponTableContainer.Factory()).setRegistryName(REFERENCE.MODID, "weapon_table"));
        registry.register(new MenuType<>(AltarInfusionContainer::new).setRegistryName(REFERENCE.MODID, "altar_infusion"));
        registry.register(new MenuType<>(BloodGrinderContainer::new).setRegistryName(REFERENCE.MODID, "blood_grinder"));
        registry.register(new MenuType<>(new MinionContainer.Factory()).setRegistryName(REFERENCE.MODID, "minion"));
        registry.register(new MenuType<>(TaskBoardContainer::new).setRegistryName(REFERENCE.MODID, "task_master"));
        registry.register(new MenuType<>(new PotionTableContainer.Factory()).setRegistryName(REFERENCE.MODID, "extended_potion_table"));
        registry.register(new MenuType<>(VampirismContainer::new).setRegistryName(REFERENCE.MODID, "vampirism"));
    }
}
