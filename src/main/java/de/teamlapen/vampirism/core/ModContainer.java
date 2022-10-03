package de.teamlapen.vampirism.core;


import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.inventory.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModContainer {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, REFERENCE.MODID);

    public static final RegistryObject<MenuType<HunterTrainerMenu>> HUNTER_TRAINER = MENUS.register("hunter_trainer", () -> new MenuType<>(HunterTrainerMenu::new));
    public static final RegistryObject<MenuType<AlchemicalCauldronMenu>> ALCHEMICAL_CAULDRON = MENUS.register("alchemical_cauldron", () -> new MenuType<>(AlchemicalCauldronMenu::new));
    public static final RegistryObject<MenuType<HunterBasicMenu>> HUNTER_BASIC = MENUS.register("hunter_basic", () -> new MenuType<>(HunterBasicMenu::new));
    public static final RegistryObject<MenuType<HunterTableMenu>> HUNTER_TABLE = MENUS.register("hunter_table", () -> new MenuType<>(new HunterTableMenu.Factory()));
    public static final RegistryObject<MenuType<WeaponTableMenu>> WEAPON_TABLE = MENUS.register("weapon_table", () -> new MenuType<>(new WeaponTableMenu.Factory()));
    public static final RegistryObject<MenuType<AltarInfusionMenu>> ALTAR_INFUSION = MENUS.register("altar_infusion", () -> new MenuType<>(AltarInfusionMenu::new));
    public static final RegistryObject<MenuType<BloodGrinderMenu>> BLOOD_GRINDER = MENUS.register("blood_grinder", () -> new MenuType<>(BloodGrinderMenu::new));
    public static final RegistryObject<MenuType<MinionContainer>> MINION = MENUS.register("minion", () -> new MenuType<>(new MinionContainer.Factory()));
    public static final RegistryObject<MenuType<TaskBoardMenu>> TASK_MASTER = MENUS.register("task_master", () -> new MenuType<>(TaskBoardMenu::new));
    public static final RegistryObject<MenuType<PotionTableMenu>> EXTENDED_POTION_TABLE = MENUS.register("extended_potion_table", () -> new MenuType<>(new PotionTableMenu.Factory()));
    public static final RegistryObject<MenuType<VampirismMenu>> VAMPIRISM = MENUS.register("vampirism", () -> new MenuType<>(VampirismMenu::new));
    public static final RegistryObject<MenuType<AlchemyTableMenu>> ALCHEMICAL_TABLE = MENUS.register("alchemical_table", () -> new MenuType<>(AlchemyTableMenu::new));


    static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}
