package de.teamlapen.vampirism.core;


import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.inventory.*;
import de.teamlapen.vampirism.inventory.diffuser.FogDiffuserMenu;
import de.teamlapen.vampirism.inventory.diffuser.GarlicDiffuserMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("deprecation")
public class ModContainer {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, REFERENCE.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<HunterTrainerMenu>> HUNTER_TRAINER = MENUS.register("hunter_trainer", () -> create(HunterTrainerMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<AlchemicalCauldronMenu>> ALCHEMICAL_CAULDRON = MENUS.register("alchemical_cauldron", () -> create(AlchemicalCauldronMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<HunterBasicMenu>> HUNTER_BASIC = MENUS.register("hunter_basic", () -> create(HunterBasicMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<HunterTableMenu>> HUNTER_TABLE = MENUS.register("hunter_table", () -> create(new HunterTableMenu.Factory()));
    public static final DeferredHolder<MenuType<?>, MenuType<WeaponTableMenu>> WEAPON_TABLE = MENUS.register("weapon_table", () -> create(new WeaponTableMenu.Factory()));
    public static final DeferredHolder<MenuType<?>, MenuType<AltarInfusionMenu>> ALTAR_INFUSION = MENUS.register("altar_infusion", () -> create(AltarInfusionMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<BloodGrinderMenu>> BLOOD_GRINDER = MENUS.register("blood_grinder", () -> create(BloodGrinderMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<MinionContainer>> MINION = MENUS.register("minion", () -> create(new MinionContainer.Factory()));
    public static final DeferredHolder<MenuType<?>, MenuType<TaskBoardMenu>> TASK_MASTER = MENUS.register("task_master", () -> create(TaskBoardMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<PotionTableMenu>> EXTENDED_POTION_TABLE = MENUS.register("extended_potion_table", () -> create(new PotionTableMenu.Factory()));
    public static final DeferredHolder<MenuType<?>, MenuType<VampirismMenu>> VAMPIRISM = MENUS.register("vampirism", () -> create(VampirismMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<AlchemyTableMenu>> ALCHEMICAL_TABLE = MENUS.register("alchemical_table", () -> create(AlchemyTableMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<VampireBeaconMenu>> VAMPIRE_BEACON = MENUS.register("vampire_beacon", () -> create(VampireBeaconMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<RevertBackMenu>> REVERT_BACK = MENUS.register("revert_back", () -> create(RevertBackMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<GarlicDiffuserMenu>> GARLIC_DIFFUSER = MENUS.register("garlic_diffuser", () -> create(new GarlicDiffuserMenu.Factory()));
    public static final DeferredHolder<MenuType<?>, MenuType<FogDiffuserMenu>> FOG_DIFFUSER = MENUS.register("fog_diffuser", () -> create(new FogDiffuserMenu.Factory()));

    private static <T extends AbstractContainerMenu> MenuType<T> create(MenuType.MenuSupplier<T> supplier) {
        return new MenuType<>(supplier, FeatureFlags.DEFAULT_FLAGS);
    }

    static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}
