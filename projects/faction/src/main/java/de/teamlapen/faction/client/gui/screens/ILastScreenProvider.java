package de.teamlapen.faction.client.gui.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@FunctionalInterface
public interface ILastScreenProvider {

    void returnToLastScreen();

    default boolean hasLastScreen() {
        return false;
    }

    ILastScreenProvider NONE = new ScreenProvider(null);

    static ILastScreenProvider of(Screen screen) {
        return new ScreenProvider(screen);
    }

    static ILastScreenProvider of() {
        return NONE;
    }

    static ILastScreenProvider current() {
        return new ScreenProvider(Minecraft.getInstance().screen);
    }

    record ScreenProvider(@Nullable Screen lastScreen) implements ILastScreenProvider {

        @Override
        public void returnToLastScreen() {
            Minecraft.getInstance().setScreen(lastScreen);
        }

        @Override
        public boolean hasLastScreen() {
            return lastScreen != null;
        }
    }


}
