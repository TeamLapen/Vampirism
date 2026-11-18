package de.teamlapen.vampirism.api.world.entity.player.vampire;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.AnimationState;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.time.MonthDay;
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
        DEFAULT(Component.translatable("wings.vampirism.default"), VIdentifier.mod("textures/entity/wings/wings.png")),
        DEV(Component.translatable("wings.vampirism.dev"), VIdentifier.mod("textures/entity/wings/wings_dev.png")),
        CASTLE_CONTEST(Component.translatable("wings.vampirism.castle_contest"), VIdentifier.mod("textures/entity/wings/wings_castle_contest.png")),
        HALLOWEEN(Component.translatable("wings.vampirism.halloween"), VIdentifier.mod("textures/entity/wings/wings_halloween.png"), MonthDay.of(10, 15), MonthDay.of(11, 5)),
        PRIDE(Component.translatable("wings.vampirism.pride"), VIdentifier.mod("textures/entity/wings/wings_pride.png"), MonthDay.of(6, 1), MonthDay.of(6, 30)),
        ;

        public static final Codec<Texture> CODEC = StringRepresentable.fromEnum(Texture::values);

        public final Component name;
        public final Identifier texture;
        /** First day (inclusive) this texture is available for everyone. {@code null} if not seasonal */
        private final @Nullable MonthDay seasonStart;
        /** Last day (inclusive) this texture is available for everyone. {@code null} if not seasonal */
        private final @Nullable MonthDay seasonEnd;

        Texture(Component name, Identifier texture) {
            this(name, texture, null, null);
        }

        Texture(Component name, Identifier texture, @Nullable MonthDay seasonStart, @Nullable MonthDay seasonEnd) {
            this.name = name;
            this.texture = texture;
            this.seasonStart = seasonStart;
            this.seasonEnd = seasonEnd;
        }

        /**
         * @return true if this texture is only available to all players during a certain time of the year
         */
        public boolean isSeasonal() {
            return this.seasonStart != null && this.seasonEnd != null;
        }

        /**
         * @return true if this is a seasonal texture and the given date is within its season. Seasons may wrap around the end of the year.
         */
        public boolean isInSeason(LocalDate date) {
            if (!FMLEnvironment.isProduction()) return true;
            if (this.seasonStart == null || this.seasonEnd == null) return false;
            MonthDay day = MonthDay.from(date);
            if (this.seasonStart.isAfter(this.seasonEnd)) {
                return !day.isBefore(this.seasonStart) || !day.isAfter(this.seasonEnd);
            }
            return !day.isBefore(this.seasonStart) && !day.isAfter(this.seasonEnd);
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
