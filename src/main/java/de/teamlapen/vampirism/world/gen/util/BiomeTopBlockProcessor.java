package de.teamlapen.vampirism.world.gen.util;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

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
    private static final Map<Block,Block> streetBlocks = new HashMap<Block, Block>(){{
        put(Blocks.SAND, Blocks.SMOOTH_SANDSTONE);
        put(Blocks.GRASS_BLOCK, Blocks.GRASS_PATH);
    }};

    private final BlockState replaceBlock;

    public BiomeTopBlockProcessor(BlockState blockState) {
        this.replaceBlock = blockState;
    }

    public BiomeTopBlockProcessor(Dynamic<?> dynamic) {
        this(BlockState.deserialize(dynamic.get("replaceBlock").orElseEmptyMap()));
    }

    @Nullable
    @Override
    public Template.BlockInfo process(@Nonnull IWorldReader worldReaderIn, @Nonnull BlockPos pos, @Nonnull Template.BlockInfo p_215194_3_, @Nonnull Template.BlockInfo blockInfo, @Nonnull PlacementSettings placementSettingsIn, @Nullable Template template) {
        if (blockInfo.state.equals(replaceBlock)) {
            BlockState topBlock = worldReaderIn.getBiome(blockInfo.pos).getSurfaceBuilderConfig().getTop();
            if(streetBlocks.containsKey(topBlock.getBlock())){
                topBlock = streetBlocks.get(topBlock.getBlock()).getDefaultState();
            }
            return new Template.BlockInfo(blockInfo.pos, topBlock, null);
        }
        return blockInfo;
    }

    @Nonnull
    @Override
    protected IStructureProcessorType getType() {
        return ModFeatures.biome_based;
    }

    @Nonnull
    @Override
    protected <T> Dynamic<T> serialize0(@Nonnull DynamicOps<T> ops) {
        return new Dynamic<>(ops,ops.createMap(ImmutableMap.of(ops.createString("replaceBlock"), BlockState.serialize(ops, this.replaceBlock).getValue())));
    }
}
