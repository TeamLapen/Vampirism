package de.teamlapen.vampirism.world.gen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.core.ModStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class RandomCandleLitProcessor extends StructureProcessor {
    public static final MapCodec<RandomCandleLitProcessor> CODEC = Codec.FLOAT.fieldOf("lit_probability").xmap(RandomCandleLitProcessor::new, entry -> entry.litProbability);
    private static final Random RANDOM = new Random();
    private final float litProbability;

    public RandomCandleLitProcessor(float litProbability) {
        this.litProbability = litProbability;
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(@NotNull LevelReader level, @NotNull BlockPos offset, @NotNull BlockPos pos, StructureTemplate.@NotNull StructureBlockInfo blockInfo, StructureTemplate.@NotNull StructureBlockInfo relativeBlockInfo, @NotNull StructurePlaceSettings settings, @Nullable StructureTemplate template) {
        BlockState blockState = relativeBlockInfo.state();
        if (blockState.getBlock() instanceof AbstractCandleBlock) {
            blockState = blockState.setValue(BlockStateProperties.LIT, RANDOM.nextFloat() <= litProbability);
        }
        return new StructureTemplate.StructureBlockInfo(relativeBlockInfo.pos(), blockState, relativeBlockInfo.nbt());
    }

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return ModStructures.RANDOM_CANDLE_LIT.get();
    }
}
