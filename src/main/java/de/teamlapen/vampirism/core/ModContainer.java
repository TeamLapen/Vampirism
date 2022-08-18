package de.teamlapen.vampirism.core;


import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.inventory.container.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModContainer {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, REFERENCE.MODID);

    public static final RegistryObject<MenuType<HunterTrainerContainer>> HUNTER_TRAINER = MENUS.register("hunter_trainer", () -> new MenuType<>(HunterTrainerContainer::new));
    public static final RegistryObject<MenuType<AlchemicalCauldronContainer>> ALCHEMICAL_CAULDRON = MENUS.register("alchemical_cauldron", () -> new MenuType<>(AlchemicalCauldronContainer::new));
    public static final RegistryObject<MenuType<HunterBasicContainer>> HUNTER_BASIC = MENUS.register("hunter_basic", () -> new MenuType<>(HunterBasicContainer::new));
    public static final RegistryObject<MenuType<HunterTableContainer>> HUNTER_TABLE = MENUS.register("hunter_table", () -> new MenuType<>(new HunterTableContainer.Factory()));
    public static final RegistryObject<MenuType<WeaponTableContainer>> WEAPON_TABLE = MENUS.register("weapon_table", () -> new MenuType<>(new WeaponTableContainer.Factory()));
    public static final RegistryObject<MenuType<AltarInfusionContainer>> ALTAR_INFUSION = MENUS.register("altar_infusion", () -> new MenuType<>(AltarInfusionContainer::new));
    public static final RegistryObject<MenuType<BloodGrinderContainer>> BLOOD_GRINDER = MENUS.register("blood_grinder", () -> new MenuType<>(BloodGrinderContainer::new));
    public static final RegistryObject<MenuType<MinionContainer>> MINION = MENUS.register("minion", () -> new MenuType<>(new MinionContainer.Factory()));
    public static final RegistryObject<MenuType<TaskBoardContainer>> TASK_MASTER = MENUS.register("task_master", () -> new MenuType<>(TaskBoardContainer::new));
    public static final RegistryObject<MenuType<PotionTableContainer>> EXTENDED_POTION_TABLE = MENUS.register("extended_potion_table", () -> new MenuType<>(new PotionTableContainer.Factory()));
    public static final RegistryObject<MenuType<VampirismContainer>> VAMPIRISM = MENUS.register("vampirism", () -> new MenuType<>(VampirismContainer::new));
    public static final RegistryObject<MenuType<AlchemyTableContainer>> ALCHEMICAL_TABLE = MENUS.register("alchemical_table", () -> new MenuType<>(AlchemyTableContainer::new));


    static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}
