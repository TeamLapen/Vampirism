package de.teamlapen.vampirism.api.world.entity.player.vampire;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.AnimationState;

import java.util.Locale;

public interface IWingsEntity {

    default boolean wingsFunctionalOpen() {
        return switch (this.getWingsState()) {
            case OPEN, OPENING, FLYING -> true;
            case CLOSING, CLOSED -> false;
        };
    }

    default boolean wingsVisualOpen() {
        return this.getWingsState() != WingsState.CLOSED;
    }

    boolean openWings();

    default void closeWings() {
        closeWings(false);
    }

    void closeWings(boolean force);

    void swingWings();
    void toggleWings();

    AnimationState flyAnimationState();

    AnimationState growAnimationState();

    WingsState getWingsState();

    float GROW_SPEED = 0.5f;
    float GROW_SECONDS = 2f;
    float GROW_TICKS = 20 * GROW_SECONDS / GROW_SPEED;

    enum WingsState implements StringRepresentable {
        CLOSED, OPENING, OPEN, FLYING, CLOSING;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    enum Texture implements StringRepresentable {
        DEFAULT(Component.translatable("wings.vampirism.default")),
        DEV(Component.translatable("wings.vampirism.dev"))
        ;

        public static final Codec<Texture> CODEC = StringRepresentable.fromEnum(Texture::values);

        public final Component name;

        Texture(Component name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
