package de.teamlapen.vampirism.mixin;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(NaturalSpawner.SpawnState.class)
public interface SpawnStateAccessor {

    @Invoker(" canSpawnForCategory(Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/world/level/ChunkPos;)Z")
    boolean canSpawnForCategory(MobCategory pCategory, ChunkPos pPos);
}
