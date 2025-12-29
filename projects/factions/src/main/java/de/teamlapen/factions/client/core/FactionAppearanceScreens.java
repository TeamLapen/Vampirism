package de.teamlapen.factions.client.core;

import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.client.gui.screens.AppearanceScreen;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class FactionAppearanceScreens {

    private static final Map<IPlayableFaction<?>, AppearanceScreenDefinition> APPEARANCE_SCREENS = new HashMap<>();

    @Nullable
    public static FactionAppearanceScreens.AppearanceScreenDefinition getProvider(IPlayableFaction<?> faction) {
        return APPEARANCE_SCREENS.get(faction);
    }

    @ApiStatus.Internal
    public static void init() {
        var event = new RegisterFactionAppearanceScreensEvent(APPEARANCE_SCREENS);
        ModLoader.postEvent(event);
    }

    public static class RegisterFactionAppearanceScreensEvent extends Event implements IModBusEvent {

        private final Map<IPlayableFaction<?>, AppearanceScreenDefinition> factionAppearanceScreens;

        @ApiStatus.Internal
        public RegisterFactionAppearanceScreensEvent(Map<IPlayableFaction<?>, AppearanceScreenDefinition> factionAppearanceScreens) {
            this.factionAppearanceScreens = factionAppearanceScreens;
        }

        public void register(Supplier<? extends IPlayableFaction<?>> faction, AppearanceScreenProvider appearanceScreenProvider, WidgetSprites buttonSprites) {
            this.factionAppearanceScreens.put(faction.get(), new AppearanceScreenDefinition(appearanceScreenProvider, buttonSprites));
        }
    }

    @FunctionalInterface
    public interface AppearanceScreenProvider {
        AppearanceScreen<?> create(@Nullable Screen backScreen);
    }

    public record AppearanceScreenDefinition(AppearanceScreenProvider provider, WidgetSprites widgetSpritesSupplier) {}
}
