package de.teamlapen.vampirism.world.gen.util;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.world.gen.VampirismFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;

public class BiomeTopBlockProcessor extends StructureProcessor {
    public static final Codec<BiomeTopBlockProcessor> CODEC = BlockState.CODEC.fieldOf("replace_block").xmap(BiomeTopBlockProcessor::new, (entry) -> entry.replaceBlock).codec();
    private static final Map<Block, Block> streetBlocks = new HashMap<>() {{
        put(Blocks.SAND, Blocks.SMOOTH_SANDSTONE);
        put(Blocks.GRASS_BLOCK, Blocks.DIRT_PATH);
    }};

    private final BlockState replaceBlock;

    /**
     * @param blockState indicator what blocks should be replaced with the biome top block
     */
    public BiomeTopBlockProcessor(BlockState blockState) {
        this.replaceBlock = blockState;
    }

    @Nullable
    public StructureTemplate.StructureBlockInfo process(@NotNull LevelReader worldReaderIn, @NotNull BlockPos pos, @NotNull BlockPos pos1, @NotNull StructureTemplate.StructureBlockInfo blockInfo, StructureTemplate.@NotNull StructureBlockInfo blockInfo1, @NotNull StructurePlaceSettings placementSettings, @Nullable StructureTemplate template) {
        if (blockInfo1.state.equals(replaceBlock)) {
            BlockState topBlock = worldReaderIn.getBlockState(blockInfo1.pos);
            if (streetBlocks.containsKey(topBlock.getBlock())) {
                topBlock = streetBlocks.get(topBlock.getBlock()).defaultBlockState();
            }
            return new StructureTemplate.StructureBlockInfo(blockInfo1.pos, topBlock, null);
        }
        return blockInfo1;
    }

    @NotNull
    @Override
    protected StructureProcessorType<?> getType() {
        return VampirismFeatures.BIOME_BASED.get();
    }

}
