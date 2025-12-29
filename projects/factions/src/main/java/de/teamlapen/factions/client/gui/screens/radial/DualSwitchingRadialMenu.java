package de.teamlapen.factions.client.gui.screens.radial;

import de.teamlapen.factions.client.gui.radialmenu.GuiRadialMenu;
import de.teamlapen.factions.client.gui.radialmenu.RadialMenu;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.KeyEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class DualSwitchingRadialMenu<T> extends GuiRadialMenu<T> {

    private boolean wasKeyReleased = false;
    private final @Nullable KeyMapping keyMapping;
    private final @NotNull Consumer<@NotNull KeyMapping> rotatingScreens;

    public DualSwitchingRadialMenu(@NotNull RadialMenu<T> radialMenu) {
        this(radialMenu, null, k -> {
        });
    }

    public DualSwitchingRadialMenu(@NotNull RadialMenu<T> radialMenu, @Nullable KeyMapping keyMapping, @NotNull Consumer<@NotNull KeyMapping> otherScreens) {
        super(radialMenu, true);
        this.keyMapping = keyMapping;
        this.rotatingScreens = otherScreens;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.wasKeyReleased && this.keyMapping != null && this.keyMapping.matches(event)) {
            this.rotatingScreens.accept(this.keyMapping);
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(@NotNull KeyEvent event) {
        if (this.keyMapping != null && this.keyMapping.matches(event)) {
            this.wasKeyReleased = true;
        }
        return super.keyReleased(event);
    }
}
