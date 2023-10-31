package de.teamlapen.vampirism.core;


import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.inventory.*;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModContainer {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, REFERENCE.MODID);

    public static final RegistryObject<MenuType<HunterTrainerMenu>> HUNTER_TRAINER = MENUS.register("hunter_trainer", () -> create(HunterTrainerMenu::new));
    public static final RegistryObject<MenuType<AlchemicalCauldronMenu>> ALCHEMICAL_CAULDRON = MENUS.register("alchemical_cauldron", () -> create(AlchemicalCauldronMenu::new));
    public static final RegistryObject<MenuType<HunterBasicMenu>> HUNTER_BASIC = MENUS.register("hunter_basic", () -> create(HunterBasicMenu::new));
    public static final RegistryObject<MenuType<HunterTableMenu>> HUNTER_TABLE = MENUS.register("hunter_table", () -> create(new HunterTableMenu.Factory()));
    public static final RegistryObject<MenuType<WeaponTableMenu>> WEAPON_TABLE = MENUS.register("weapon_table", () -> create(new WeaponTableMenu.Factory()));
    public static final RegistryObject<MenuType<AltarInfusionMenu>> ALTAR_INFUSION = MENUS.register("altar_infusion", () -> create(AltarInfusionMenu::new));
    public static final RegistryObject<MenuType<BloodGrinderMenu>> BLOOD_GRINDER = MENUS.register("blood_grinder", () -> create(BloodGrinderMenu::new));
    public static final RegistryObject<MenuType<MinionContainer>> MINION = MENUS.register("minion", () -> create(new MinionContainer.Factory()));
    public static final RegistryObject<MenuType<TaskBoardMenu>> TASK_MASTER = MENUS.register("task_master", () -> create(TaskBoardMenu::new));
    public static final RegistryObject<MenuType<PotionTableMenu>> EXTENDED_POTION_TABLE = MENUS.register("extended_potion_table", () -> create(new PotionTableMenu.Factory()));
    public static final RegistryObject<MenuType<VampirismMenu>> VAMPIRISM = MENUS.register("vampirism", () -> create(VampirismMenu::new));
    public static final RegistryObject<MenuType<AlchemyTableMenu>> ALCHEMICAL_TABLE = MENUS.register("alchemical_table", () -> create(AlchemyTableMenu::new));
    public static final RegistryObject<MenuType<VampireBeaconMenu>> VAMPIRE_BEACON = MENUS.register("vampire_beacon", () -> create(VampireBeaconMenu::new));

    private static <T extends AbstractContainerMenu> MenuType<T> create(MenuType.MenuSupplier<T> supplier) {
        return new MenuType<>(supplier, FeatureFlags.DEFAULT_FLAGS);
    }

    static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}
