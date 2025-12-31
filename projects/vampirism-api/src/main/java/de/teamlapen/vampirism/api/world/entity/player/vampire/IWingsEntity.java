package de.teamlapen.vampirism.api.world.entity.player.vampire;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.AnimationState;

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

    float GROW_SPEED = 0.5f;
    float GROW_SECONDS = 1f;
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
