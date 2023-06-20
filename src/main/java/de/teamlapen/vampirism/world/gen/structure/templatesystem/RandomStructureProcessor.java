package de.teamlapen.vampirism.world.gen.structure.templatesystem;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.world.gen.VampirismFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * works the same as a {@link RuleProcessor} but for {@link RandomBlockStateRule} instead of {@link ProcessorRule}
 */
public class RandomStructureProcessor extends RuleProcessor {
    public static final Codec<RandomStructureProcessor> CODEC = RandomBlockStateRule.CODEC.listOf().fieldOf("rules").xmap(RandomStructureProcessor::new, rule -> rule.rules).codec();
    private final @NotNull ImmutableList<RandomBlockStateRule> rules;

    public RandomStructureProcessor(@NotNull List<RandomBlockStateRule> rules) {
        super(Collections.emptyList());
        this.rules = ImmutableList.copyOf(rules);
    }

    @Nullable
    public StructureTemplate.StructureBlockInfo process(@NotNull LevelReader worldReaderIn, @NotNull BlockPos pos, @NotNull BlockPos pos2, @NotNull StructureTemplate.StructureBlockInfo blockInfo, StructureTemplate.@NotNull StructureBlockInfo blockInfo1, @NotNull StructurePlaceSettings placementSettings, @Nullable StructureTemplate template) {
        RandomSource random = RandomSource.create(Mth.getSeed(blockInfo1.pos()));
        BlockState blockstate = worldReaderIn.getBlockState(blockInfo1.pos());

        for (RandomBlockStateRule ruleEntry : this.rules) {
            if (ruleEntry.test(blockInfo1.state(), blockstate, blockInfo.pos(), blockInfo1.pos(), pos2, random)) { // ruleEntry#test
                Pair<BlockState, RuleBlockEntityModifier> pair = ruleEntry.getOutput();
                return new StructureTemplate.StructureBlockInfo(blockInfo1.pos(), pair.getLeft(), pair.getRight().apply(random, null));
            }
        }

        return blockInfo1;
    }

    @NotNull
    @Override
    protected StructureProcessorType<?> getType() {
        return VampirismFeatures.RANDOM_SELECTOR.get();
    }

}
