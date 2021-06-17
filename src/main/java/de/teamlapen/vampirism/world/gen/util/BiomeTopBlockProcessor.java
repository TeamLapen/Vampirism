package de.teamlapen.vampirism.world.gen.util;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.core.ModFeatures;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class BiomeTopBlockProcessor extends StructureProcessor {
    public static final Codec<BiomeTopBlockProcessor> CODEC = BlockState.CODEC.fieldOf("replace_block").xmap(BiomeTopBlockProcessor::new, (entry) -> entry.replaceBlock).codec();
    private static final Map<Block, Block> streetBlocks = new HashMap<Block, Block>() {{
        put(Blocks.SAND, Blocks.SMOOTH_SANDSTONE);
        put(Blocks.GRASS_BLOCK, Blocks.GRASS_PATH);
    }};

    private final BlockState replaceBlock;

    public BiomeTopBlockProcessor(BlockState blockState) {
        this.replaceBlock = blockState;
    }

    @Nullable
    public Template.BlockInfo process(@Nonnull IWorldReader worldReaderIn, @Nonnull BlockPos pos, @Nonnull BlockPos pos1, @Nonnull Template.BlockInfo blockInfo, Template.BlockInfo blockInfo1, @Nonnull PlacementSettings placementSettings, @Nullable Template template) {
        if (blockInfo1.state.equals(replaceBlock)) {
            BlockState topBlock = worldReaderIn.getBiome(blockInfo1.pos).getGenerationSettings().getSurfaceBuilderConfig().getTop();
            if (streetBlocks.containsKey(topBlock.getBlock())) {
                topBlock = streetBlocks.get(topBlock.getBlock()).getDefaultState();
            }
            return new Template.BlockInfo(blockInfo1.pos, topBlock, null);
        }
        return blockInfo1;
    }

    @Nonnull
    @Override
    protected IStructureProcessorType<?> getType() {
        return ModFeatures.biome_based;
    }

}
