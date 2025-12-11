package de.teamlapen.vampirism.misc.injection.client;

import de.teamlapen.vampirism.misc.extension.client.IHunterPlayerState;
import de.teamlapen.vampirism.misc.extension.client.IVampirePlayerState;
import net.minecraft.world.entity.ambient.Bat;
import org.jetbrains.annotations.Nullable;

public interface AvatarRenderStateMock extends IVampirePlayerState, IHunterPlayerState {

    @Override
    default int vampirism$vampire$getEyeType() {
        return 0;
    }

    @Override
    default void vampirism$vampire$setEyeType(int type) {

    }

    @Override
    default int vampirism$vampire$getFangType() {
        return 0;
    }

    @Override
    default void vampirism$vampire$setFangType(int type) {

    }

    @Override
    default boolean vampirism$vampire$getGlowingEyes() {
        return false;
    }

    @Override
    default void vampirism$vampire$setGlowingEyes(boolean glowing) {

    }

    @Override
    default boolean vampirism$vampire$isDisguised() {
        return false;
    }

    @Override
    default void vampirism$vampire$setDisguised(boolean disguised) {

    }

    @Override
    default int vampirism$vampire$level() {
        return 0;
    }

    @Override
    default void vampirism$vampire$setVampireLevel(int level) {

    }

    @Override
    default boolean vampirism$vampire$isDbno() {
        return false;
    }

    @Override
    default void vampirism$vampire$setDbno(boolean dbno) {

    }

    @Override
    default boolean vampirism$vampire$sleepingInCoffin() {
        return false;
    }

    @Override
    default void vampirism$vampire$setSleepingInCoffin(boolean sleepingInCoffin) {

    }

    @Override
    default boolean vampirism$hunter$fullHunterCoat() {
        return false;
    }

    @Override
    default boolean vampirism$hunter$isDisguised() {
        return false;
    }

    @Override
    default void vampirism$hunter$setFullHunterCoat(boolean fullHunterCoat) {

    }

    @Override
    default void vampirism$hunter$setDisguised(boolean disguised) {

    }

    @Override
    default @Nullable Bat vampirism$vampire$getBat() {
        return null;
    }

    @Override
    default boolean vampirism$vampire$invisible() {
        return false;
    }

    @Override
    default void vampirism$vampire$setBat(@Nullable Bat bat) {

    }

    @Override
    default void vampirism$vampire$setInvisible(boolean invisible) {

    }
}
