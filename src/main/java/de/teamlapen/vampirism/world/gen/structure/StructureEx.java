package de.teamlapen.vampirism.world.gen.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiConsumer;

public abstract class StructureEx extends Structure {

    protected StructureEx(StructureSettings pSettings) {
        super(pSettings);
    }

    protected Optional<GenerationStub> onSurface(@NotNull GenerationContext pContext, BiConsumer<StructurePiecesBuilder, BlockPos> builder) {
        ChunkPos chunkPos = pContext.chunkPos();
        int y = ConstantHeight.of(VerticalAnchor.absolute(0)).sample(pContext.random(), new WorldGenerationContext(pContext.chunkGenerator(), pContext.heightAccessor()));
        y += pContext.chunkGenerator().getFirstFreeHeight(chunkPos.getMiddleBlockX(), chunkPos.getMiddleBlockZ(), Heightmap.Types.WORLD_SURFACE_WG, pContext.heightAccessor(), pContext.randomState());
        BlockPos pos = new BlockPos(chunkPos.getMinBlockX(), Math.max(63, y), chunkPos.getMinBlockZ());
        return Optional.of(new GenerationStub(pos, b -> builder.accept(b, pos.above(0))));
    }
}