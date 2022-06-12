package de.teamlapen.vampirism.world.gen.util;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.world.gen.VampirismFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * works the same as a {@link RuleProcessor} but for {@link RandomBlockState} instead of {@link ProcessorRule}
 */
public class RandomStructureProcessor extends RuleProcessor {
    public static final Codec<RandomStructureProcessor> CODEC = RandomBlockState.CODEC.listOf().fieldOf("rules").xmap(RandomStructureProcessor::new, rule -> rule.rules).codec();
    private final ImmutableList<RandomBlockState> rules;

    public RandomStructureProcessor(List<RandomBlockState> rules) {
        super(Collections.emptyList());
        this.rules = ImmutableList.copyOf(rules);
    }

    @Nullable
    public StructureTemplate.StructureBlockInfo process(LevelReader worldReaderIn, @Nonnull BlockPos pos, @Nonnull BlockPos pos2, @Nonnull StructureTemplate.StructureBlockInfo blockInfo, StructureTemplate.StructureBlockInfo blockInfo1, @Nonnull StructurePlaceSettings placementSettings, @Nullable StructureTemplate template) {
        Random random = new Random(Mth.getSeed(blockInfo1.pos));
        BlockState blockstate = worldReaderIn.getBlockState(blockInfo1.pos);

        for (RandomBlockState ruleEntry : this.rules) {
            if (ruleEntry.test(blockInfo1.state, blockstate, blockInfo.pos, blockInfo1.pos, pos2, random)) { // ruleEntry#test
                Pair<BlockState, Optional<CompoundTag>> pair = ruleEntry.getOutput();
                return new StructureTemplate.StructureBlockInfo(blockInfo1.pos, pair.getLeft(), pair.getRight().orElse(null));
            }
        }

        return blockInfo1;
    }

    @Nonnull
    @Override
    protected StructureProcessorType<?> getType() {
        return VampirismFeatures.RANDOM_SELECTOR.get();
    }

}
