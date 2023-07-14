package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.entity.converted.CurableConvertedCreature;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.NotNull;

public interface ConvertedCreature<T extends PathfinderMob> extends IConvertedCreature<T> {


    default void tickC() {
        if (this.getRepresentingEntity().level().isClientSide && this.getRepresentingEntity().level().getDifficulty() == Difficulty.PEACEFUL) {
            this.getRepresentingEntity().discard();
        }
    }

    default void readAdditionalSaveDataC(@NotNull CompoundTag compound) {
        if (compound.contains("convertedOverlay")) {
            this.data().texture = new ResourceLocation(compound.getString("convertedOverlay"));
        } else {
            this.data().texture = null;
        }
    }

    default void addAdditionalSaveDataC(@NotNull CompoundTag compound) {
        compound.putString("convertedOverlay", this.data().texture.toString());
    }
}
