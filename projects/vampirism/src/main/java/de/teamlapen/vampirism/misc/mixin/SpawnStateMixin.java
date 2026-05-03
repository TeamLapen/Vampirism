package de.teamlapen.vampirism.misc.mixin;

import de.teamlapen.vampirism.misc.extension.ISpawnState;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(NaturalSpawner.SpawnState.class)
public abstract class SpawnStateMixin implements ISpawnState {

    @Shadow
    protected abstract boolean canSpawnForCategoryLocal(MobCategory mobCategory, ChunkPos chunkPos);

    @Unique
    @Override
    public boolean vampirism$canSpawnForCategoryLocalI(MobCategory category, ChunkPos chunkPos) {
        return canSpawnForCategoryLocal(category, chunkPos);
    }
}
