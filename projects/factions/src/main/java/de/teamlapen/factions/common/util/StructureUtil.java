package de.teamlapen.factions.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.Optional;

public class StructureUtil {

    public static boolean isInsideStructure(Level w, BlockPos p, TagKey<Structure> s) {
        return getStructureStartAt(w, p, s).isPresent();
    }

    public static Optional<StructureStart> getStructureStartAt(Level level, BlockPos pos, TagKey<Structure> structureTag) {
        if (level instanceof ServerLevel serverLevel && serverLevel.isLoaded(pos)) {
            Registry<Structure> registry = serverLevel.registryAccess().lookupOrThrow(Registries.STRUCTURE);
            return serverLevel.structureManager().startsForStructure(new ChunkPos(pos), structure -> {
                return registry.get(registry.getId(structure)).map(a -> a.is(structureTag)).orElse(false);
            }).stream().findFirst();
        }
        return Optional.empty();
    }
}
