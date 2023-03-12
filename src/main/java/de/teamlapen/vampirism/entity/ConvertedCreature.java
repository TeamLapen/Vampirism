package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.world.Difficulty;

public interface ConvertedCreature<T extends CreatureEntity> extends IConvertedCreature<T> {

    default void tickC() {
        if (this.getRepresentingEntity().level.isClientSide && this.getRepresentingEntity().level.getDifficulty() == Difficulty.PEACEFUL) {
            this.getRepresentingEntity().remove();
        }
    }
}
