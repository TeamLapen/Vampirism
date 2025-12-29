package de.teamlapen.vampirism.misc.injection.client;

import de.teamlapen.vampirism.misc.extension.client.IHunterPlayerState;
import de.teamlapen.vampirism.misc.extension.client.IVampirePlayerState;
import net.minecraft.world.entity.ambient.Bat;
import org.jetbrains.annotations.Nullable;

@Deprecated
public interface AvatarRenderStateVampirismMock extends IVampirePlayerState, IHunterPlayerState {

    @Override
    default int vampirism$vampire$getEyeType() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default void vampirism$vampire$setEyeType(int type) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default int vampirism$vampire$getFangType() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default void vampirism$vampire$setFangType(int type) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default boolean vampirism$vampire$getGlowingEyes() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default void vampirism$vampire$setGlowingEyes(boolean glowing) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default boolean vampirism$vampire$isDisguised() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default void vampirism$vampire$setDisguised(boolean disguised) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default int vampirism$vampire$level() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default void vampirism$vampire$setVampireLevel(int level) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default boolean vampirism$vampire$isDbno() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default void vampirism$vampire$setDbno(boolean dbno) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default boolean vampirism$vampire$sleepingInCoffin() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default void vampirism$vampire$setSleepingInCoffin(boolean sleepingInCoffin) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default boolean vampirism$hunter$fullHunterCoat() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default boolean vampirism$hunter$isDisguised() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default void vampirism$hunter$setFullHunterCoat(boolean fullHunterCoat) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default void vampirism$hunter$setDisguised(boolean disguised) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default @Nullable Bat vampirism$vampire$getBat() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default boolean vampirism$vampire$invisible() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default void vampirism$vampire$setBat(@Nullable Bat bat) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default void vampirism$vampire$setInvisible(boolean invisible) {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
