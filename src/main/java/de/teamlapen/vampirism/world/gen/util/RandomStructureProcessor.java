package de.teamlapen.vampirism.world.gen.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;

import de.teamlapen.vampirism.core.ModFeatures;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.RuleStructureProcessor;
import net.minecraft.world.gen.feature.template.Template;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class RandomStructureProcessor extends RuleStructureProcessor {
    private final List<RandomBlockState> rules;

    public RandomStructureProcessor(List<RandomBlockState> rules) {
        super(Lists.newArrayList(rules));
        this.rules = rules;
    }

    public RandomStructureProcessor(Dynamic<?> dynamic) {
        this(dynamic.get("rules").asList(RandomBlockState::deserialize));
    }

    @Nullable
    public Template.BlockInfo process(IWorldReader worldReaderIn, BlockPos pos, Template.BlockInfo p_215194_3_, Template.BlockInfo blockInfo, PlacementSettings placementSettingsIn) {
        Random random = new Random(MathHelper.getPositionRandom(blockInfo.pos));
        BlockState blockstate = worldReaderIn.getBlockState(blockInfo.pos);

        for(RandomBlockState ruleEntry : this.rules) {
            if (ruleEntry.test(blockInfo.state, blockstate, random)) {
                Pair<BlockState, CompoundNBT> pair = ruleEntry.getOutput();
                return new Template.BlockInfo(blockInfo.pos, pair.getFirst(), pair.getSecond());
            }
        }

        return blockInfo;
    }

    @Nonnull
    @Override
    protected IStructureProcessorType getType() {
        return ModFeatures.random_selector;
    }

}
