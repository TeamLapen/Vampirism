package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.PathfinderMob;

public interface ConvertedCreature<T extends PathfinderMob> extends IConvertedCreature<T> {

    default void tickC() {
        if (this.getRepresentingEntity().level().isClientSide && this.getRepresentingEntity().level().getDifficulty() == Difficulty.PEACEFUL) {
            this.getRepresentingEntity().discard();
        }
    }
}
