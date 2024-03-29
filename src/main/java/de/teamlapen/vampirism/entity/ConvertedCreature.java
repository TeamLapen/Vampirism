package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.NotNull;

public interface ConvertedCreature<T extends PathfinderMob> extends IConvertedCreature<T> {


    default void tickC() {
        if (this.asEntity().level().isClientSide && this.asEntity().level().getDifficulty() == Difficulty.PEACEFUL) {
            this.asEntity().discard();
        }
    }

    default void readAdditionalSaveDataC(@NotNull CompoundTag compound) {
    }

    default void addAdditionalSaveDataC(@NotNull CompoundTag compound) {
    }
}
