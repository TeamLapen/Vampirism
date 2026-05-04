package de.teamlapen.vampirism.common.world.structures.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.common.core.ModStructures;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

import java.util.Optional;

public class SingleOccurrenceStructurePlacement extends StructurePlacement {
    public static final MapCodec<SingleOccurrenceStructurePlacement> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(
                Codec.INT.fieldOf("chunk_x").forGetter((placement) -> placement.chunkX),
                Codec.INT.fieldOf("chunk_z").forGetter((placement) -> placement.chunkZ),
                Vec3i.CODEC.optionalFieldOf("locate_offset", Vec3i.ZERO).forGetter(SingleOccurrenceStructurePlacement::locateOffset),
                StructurePlacement.FrequencyReductionMethod.CODEC.optionalFieldOf("frequency_reduction_method", StructurePlacement.FrequencyReductionMethod.DEFAULT).forGetter(SingleOccurrenceStructurePlacement::frequencyReductionMethod),
                Codec.floatRange(0.0F, 1.0F).optionalFieldOf("frequency", 1.0F).forGetter(SingleOccurrenceStructurePlacement::frequency),
                Codec.INT.fieldOf("salt").forGetter(SingleOccurrenceStructurePlacement::salt),
                StructurePlacement.ExclusionZone.CODEC.optionalFieldOf("exclusion_zone").forGetter(SingleOccurrenceStructurePlacement::exclusionZone)
        ).apply(instance, SingleOccurrenceStructurePlacement::new);
    });

    private final int chunkX;
    private final int chunkZ;

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "deprecation"})
    public SingleOccurrenceStructurePlacement(int chunkX, int chunkZ, Vec3i locateOffset, FrequencyReductionMethod frequencyReductionMethod, float frequency, int salt, Optional<ExclusionZone> exclusionZone) {
        super(locateOffset, frequencyReductionMethod, frequency, salt, exclusionZone);
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public SingleOccurrenceStructurePlacement(int chunkX, int chunkZ) {
        this(chunkX, chunkZ, Vec3i.ZERO, FrequencyReductionMethod.DEFAULT, 1.0F, 0, Optional.empty());
    }

    @Override
    protected boolean isPlacementChunk(ChunkGeneratorStructureState chunkGeneratorStructureState, int x, int z) {
        return x == this.chunkX && z == this.chunkZ;
    }

    @Override
    public StructurePlacementType<?> type() {
        return ModStructures.SINGLE_OCCURRENCE_PLACEMENT_TYPE.get();
    }
}
