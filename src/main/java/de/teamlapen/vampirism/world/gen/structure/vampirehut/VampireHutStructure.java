package de.teamlapen.vampirism.world.gen.structure.vampirehut;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.core.ModStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class VampireHutStructure extends Structure {

    public static final MapCodec<VampireHutStructure> CODEC = simpleCodec(VampireHutStructure::new);

    public VampireHutStructure(Structure.StructureSettings settings) {
        super(settings);
    }

    @Override
    protected @NotNull Optional<GenerationStub> findGenerationPoint(@NotNull GenerationContext pContext) {
        return onTopOfChunkCenter(pContext, Heightmap.Types.WORLD_SURFACE_WG, (builder) -> {
            this.generatePieces(builder, pContext);
        });
    }

    private void generatePieces(StructurePiecesBuilder builder, Structure.GenerationContext context) {
        ChunkPos chunk = context.chunkPos();
        WorldgenRandom random = context.random();
        VampireHutPieces.addPieces(context.structureTemplateManager(), builder, random, new BlockPos(chunk.getMinBlockX(), 90, chunk.getMinBlockZ()));
    }

    @Override
    public @NotNull StructureType<?> type() {
        return ModStructures.VAMPIRE_HUT_TYPE.get();
    }

}
