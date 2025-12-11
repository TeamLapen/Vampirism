package de.teamlapen.factions.common.core;

import de.teamlapen.factions.api.util.REFERENCE;
import de.teamlapen.factions.common.inventory.MinionContainer;
import de.teamlapen.factions.common.inventory.TaskBoardMenu;
import de.teamlapen.factions.common.inventory.FactionMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FactionMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, REFERENCE.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<MinionContainer>> MINION = MENUS.register("minion", () -> create(new MinionContainer.Factory()));
    public static final DeferredHolder<MenuType<?>, MenuType<TaskBoardMenu>> TASK_MASTER = MENUS.register("task_master", () -> create(TaskBoardMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<FactionMenu>> FACTION_MENU = MENUS.register("faction_menu", () -> create(FactionMenu::new));

    private static <T extends AbstractContainerMenu> MenuType<T> create(MenuType.MenuSupplier<T> supplier) {
        return new MenuType<>(supplier, FeatureFlags.DEFAULT_FLAGS);
    }

    static void register(IEventBus bus) {
        MENUS.register(bus);
    }


}
