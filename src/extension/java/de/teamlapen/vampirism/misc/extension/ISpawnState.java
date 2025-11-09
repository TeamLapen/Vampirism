package de.teamlapen.vampirism.misc.extension;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;

public interface ISpawnState {

    boolean vampirism$canSpawnForCategoryLocalI(MobCategory category, ChunkPos chunkPos);
}
