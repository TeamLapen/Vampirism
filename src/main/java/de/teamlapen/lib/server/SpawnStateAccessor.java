package de.teamlapen.lib.server;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;

/**
 * injected into {@link net.minecraft.world.level.NaturalSpawner.SpawnState} in {@link de.teamlapen.vampirism.common.mixin.SpawnStateMixin}
 */
public interface SpawnStateAccessor {

    boolean vampirism$canSpawnForCategoryLocalI(MobCategory category, ChunkPos chunkPos);
}
