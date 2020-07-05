package de.teamlapen.vampirism.world.gen.util;

import com.google.common.collect.ImmutableList;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.core.ModFeatures;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomStructureProcessor extends RuleStructureProcessor {
    public static final Codec<RandomStructureProcessor> CODEC = RandomBlockState.CODEC.listOf().fieldOf("rules").xmap(RandomStructureProcessor::new, rule -> rule.rules).codec();
    private final ImmutableList<RandomBlockState> rules;

    public RandomStructureProcessor(List<RandomBlockState> rules) {
        super(Collections.emptyList());
        this.rules = ImmutableList.copyOf(rules);
    }

    @Nullable
    public Template.BlockInfo process(IWorldReader worldReaderIn, @Nonnull BlockPos pos, @Nonnull BlockPos pos2, @Nonnull Template.BlockInfo blockInfo, Template.BlockInfo blockInfo1, @Nonnull PlacementSettings placementSettings, @Nullable Template template) {
        Random random = new Random(MathHelper.getPositionRandom(blockInfo1.pos));
        BlockState blockstate = worldReaderIn.getBlockState(blockInfo1.pos);

        for(RandomBlockState ruleEntry : this.rules) {
            if (ruleEntry.func_237110_a_(blockInfo1.state, blockstate, blockInfo.pos, blockInfo1.pos, pos2, random)) { // ruleEntry#test
                Pair<BlockState, CompoundNBT> pair = ruleEntry.getOutput();
                return new Template.BlockInfo(blockInfo1.pos, pair.getFirst(), pair.getSecond());
            }
        }

        return blockInfo1;
    }

    @Nonnull
    @Override
    protected IStructureProcessorType<?> getType() {
        return ModFeatures.random_selector;
    }

}
