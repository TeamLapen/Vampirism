package de.teamlapen.lib.util;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;

public interface SpawnStateAccessor {

    boolean vampirism$canSpawnForCategoryLocalI(MobCategory category, ChunkPos chunkPos);
}
