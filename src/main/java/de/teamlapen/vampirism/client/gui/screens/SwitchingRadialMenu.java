package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.lib.lib.client.gui.screens.radialmenu.GuiRadialMenu;
import de.teamlapen.lib.lib.client.gui.screens.radialmenu.RadialMenu;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public abstract class SwitchingRadialMenu<T> extends GuiRadialMenu<T> {

    private final @Nullable KeyMapping keyMapping;
    private final @NotNull Function<@NotNull KeyMapping, @Nullable SwitchingRadialMenu<?>> rotatingScreens;

    public SwitchingRadialMenu(@NotNull RadialMenu<T> radialMenu) {
        this(radialMenu, null, k -> null);
    }

    public SwitchingRadialMenu(@NotNull RadialMenu<T> radialMenu, @Nullable KeyMapping keyMapping, @NotNull Function<@NotNull KeyMapping, @Nullable SwitchingRadialMenu<?>> otherScreens) {
        super(radialMenu);
        this.keyMapping = keyMapping;
        this.rotatingScreens = otherScreens;
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (this.keyMapping != null && this.keyMapping.matches(key, scanCode)) {
            SwitchingRadialMenu<?> newScreen = this.rotatingScreens.apply(this.keyMapping);
            if (newScreen != null) {
                this.minecraft.setScreen(newScreen);
                return true;
            }
        }
        return super.keyPressed(key, scanCode, modifiers);
    }


}
