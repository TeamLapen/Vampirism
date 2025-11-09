package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.ISpawnState;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;

public interface ISpawnStateAccessorMock extends ISpawnState {
    @Override
    default boolean vampirism$canSpawnForCategoryLocalI(MobCategory category, ChunkPos chunkPos) {
        return false;
    }
}
