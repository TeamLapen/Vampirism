package de.teamlapen.vampirism.common.mixin;

import de.teamlapen.lib.server.SpawnStateAccessor;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(NaturalSpawner.SpawnState.class)
public abstract class SpawnStateMixin implements SpawnStateAccessor {

    @Shadow abstract boolean canSpawnForCategoryLocal(MobCategory p_362658_, ChunkPos p_360784_);

    @Unique
    @Override
    public boolean vampirism$canSpawnForCategoryLocalI(MobCategory category, ChunkPos chunkPos) {
        return canSpawnForCategoryLocal(category, chunkPos);
    }
}
