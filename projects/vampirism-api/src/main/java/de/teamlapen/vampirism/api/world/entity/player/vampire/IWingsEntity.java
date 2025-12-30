package de.teamlapen.vampirism.api.world.entity.player.vampire;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.AnimationState;
import org.jspecify.annotations.NonNull;

import java.util.Locale;

public interface IWingsEntity {

    boolean wingsFunctionalOpen();

    boolean wingsVisualOpen();

    boolean openWings();

    void closeWings();

    void swingWings();
    void toggleWings();

    AnimationState flyAnimationState();

    AnimationState growAnimationState();

    WingsState getWingsState();

    float GROW_SPEED = 0.25f;
    float GROW_SECONDS = 1f;
    float GROW_TICKS = 20 * GROW_SECONDS / GROW_SPEED;

    enum WingsState implements StringRepresentable {
        CLOSED, OPENING, OPEN, FLYING, CLOSING;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
