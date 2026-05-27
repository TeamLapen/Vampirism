package de.teamlapen.faction.client.gui.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@FunctionalInterface
public interface ILastScreenProvider {

    void returnToLastScreen();

    static ILastScreenProvider of(Screen screen) {
        return new ScreenProvider(screen);
    }

    static ILastScreenProvider current() {
        return new ScreenProvider(Minecraft.getInstance().screen);
    }

    record ScreenProvider(@Nullable Screen lastScreen) implements ILastScreenProvider {

        @Override
        public void returnToLastScreen() {
            Minecraft.getInstance().setScreen(lastScreen);
        }
    }

}
