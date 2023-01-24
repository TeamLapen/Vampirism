package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.lib.lib.client.gui.screens.radialmenu.GuiRadialMenu;
import de.teamlapen.lib.lib.client.gui.screens.radialmenu.RadialMenu;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class SwitchingRadialMenu<T> extends GuiRadialMenu<T> {

    private boolean wasKeyReleased = false;
    private final @Nullable KeyMapping keyMapping;
    private final @NotNull Consumer<@NotNull KeyMapping> rotatingScreens;

    public SwitchingRadialMenu(@NotNull RadialMenu<T> radialMenu) {
        this(radialMenu, null, k -> {});
    }

    public SwitchingRadialMenu(@NotNull RadialMenu<T> radialMenu, @Nullable KeyMapping keyMapping, @NotNull Consumer<@NotNull KeyMapping> otherScreens) {
        super(radialMenu);
        this.keyMapping = keyMapping;
        this.rotatingScreens = otherScreens;
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (this.wasKeyReleased && this.keyMapping != null && this.keyMapping.matches(key, scanCode)) {
            this.rotatingScreens.accept(this.keyMapping);
            return true;
        }
        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        if (this.keyMapping != null && this.keyMapping.matches(pKeyCode, pScanCode)) {
            this.wasKeyReleased = true;
        }
        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }
}
