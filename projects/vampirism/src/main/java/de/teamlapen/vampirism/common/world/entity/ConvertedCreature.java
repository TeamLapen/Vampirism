package de.teamlapen.vampirism.common.world.entity;

import de.teamlapen.vampirism.api.world.entity.convertible.IConvertedCreature;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

public interface ConvertedCreature<T extends PathfinderMob> extends IConvertedCreature<T> {


    default void tickC() {
        if (this.asEntity().level().isClientSide() && this.asEntity().level().getDifficulty() == Difficulty.PEACEFUL) {
            this.asEntity().discard();
        }
    }

    default void readAdditionalSaveDataC(@NotNull ValueInput input) {
    }

    default void addAdditionalSaveDataC(@NotNull ValueOutput output) {
    }
}
