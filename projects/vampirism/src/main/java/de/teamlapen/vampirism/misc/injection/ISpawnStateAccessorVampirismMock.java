package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.ISpawnState;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;

@Deprecated
public interface ISpawnStateAccessorVampirismMock extends ISpawnState {
    @Override
    default boolean vampirism$canSpawnForCategoryLocalI(MobCategory category, ChunkPos chunkPos) {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
