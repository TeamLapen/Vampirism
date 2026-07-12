package de.teamlapen.vampirism.common.world.structures.crypt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.common.core.ModStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class CryptStructure extends Structure {

    public static final MapCodec<CryptStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            settingsCodec(instance),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(s -> s.startPool),
            Codec.intRange(0, JigsawStructure.MAX_DEPTH).fieldOf("max_depth").forGetter(s -> s.maxDepth)
    ).apply(instance, CryptStructure::new));

    private final Holder<StructureTemplatePool> startPool;
    private final int maxDepth;

    public CryptStructure(StructureSettings settings, Holder<StructureTemplatePool> startPool, int maxDepth) {
        super(settings);
        this.startPool = startPool;
        this.maxDepth = maxDepth;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext generationContext) {
        ChunkPos chunkPos = generationContext.chunkPos();
        BlockPos startPos = new BlockPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ());
        return JigsawPlacement.addPieces(generationContext, startPool, Optional.empty(), maxDepth, startPos, false, Optional.of(Heightmap.Types.WORLD_SURFACE_WG), new JigsawStructure.MaxDistance(80, 80), PoolAliasLookup.EMPTY, DimensionPadding.ZERO, LiquidSettings.IGNORE_WATERLOGGING)
                .map(stub -> new GenerationStub(stub.position(), builder -> {
                    StructurePiecesBuilder jigsawBuilder = new StructurePiecesBuilder();
                    List<StructurePiece> pieces = stub.generator().map(generator -> {
                        generator.accept(jigsawBuilder);
                        return jigsawBuilder;
                    }, Function.identity()).build().pieces();
                    for (int i = 0; i < pieces.size(); i++) {
                        if (pieces.get(i) instanceof PoolElementStructurePiece piece) {
                            builder.addPiece(new CryptPiece(generationContext.structureTemplateManager(), piece, LiquidSettings.IGNORE_WATERLOGGING, i == 0 ? terrainAdaptation() : TerrainAdjustment.NONE));
                        } else {
                            builder.addPiece(pieces.get(i));
                        }
                    }
                }));
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.CRYPT_TYPE.get();
    }
}
