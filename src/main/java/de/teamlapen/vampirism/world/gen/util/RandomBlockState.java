package de.teamlapen.vampirism.world.gen.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.gen.feature.template.AlwaysTrueTest;
import net.minecraft.world.gen.feature.template.PosRuleTest;
import net.minecraft.world.gen.feature.template.RuleEntry;
import net.minecraft.world.gen.feature.template.RuleTest;

import java.util.Optional;
import java.util.Random;

public class RandomBlockState extends RuleEntry {
    @SuppressWarnings("CodeBlock2Expr")
    public static final Codec<RandomBlockState> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(RuleTest.field_237127_c_.fieldOf("input_predicate").forGetter((getter) -> {
            return getter.inputPredicate;
        }), RuleTest.field_237127_c_.fieldOf("location_predicate").forGetter(entry -> {
            return entry.locationPredicate;
        }), PosRuleTest.field_237102_c_.fieldOf("position_predicate").forGetter(entry -> {
            return entry.field_237109_d_;
        }), BlockState.BLOCKSTATE_CODEC.fieldOf("output_state_1").forGetter(entry -> {
            return entry.outputState;
        }), BlockState.BLOCKSTATE_CODEC.fieldOf("output_state_2").forGetter(entry -> {
            return entry.outputState2;
        }), CompoundNBT.field_240597_a_.optionalFieldOf("output_nbt_1").forGetter(entry -> {
            return Optional.ofNullable(entry.outputNbt);
        }), CompoundNBT.field_240597_a_.optionalFieldOf("output_nbt_2").forGetter(entry -> {
            return Optional.ofNullable(entry.outputNbt2);
        })).apply(instance, RandomBlockState::new);
    });
    private static final Random RNG = new Random();

    private final BlockState outputState2;
    private final CompoundNBT outputNbt2;

    public RandomBlockState(RuleTest inputPredicate, RuleTest locationPredicate, BlockState outputState, BlockState outputState2) {
        this(inputPredicate, locationPredicate, AlwaysTrueTest.field_237100_b_, outputState, outputState2, Optional.empty(), Optional.empty());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public RandomBlockState(RuleTest inputPredicate, RuleTest locationPredicate, PosRuleTest posRuleTest, BlockState outputState, BlockState outputState2, Optional<CompoundNBT> outputNbt, Optional<CompoundNBT> outputNbt2) {
        super(inputPredicate, locationPredicate, AlwaysTrueTest.field_237100_b_, outputState, outputNbt);
        this.outputState2 = outputState2;
        this.outputNbt2 = outputNbt2.orElse(null);
    }

    public Pair<BlockState, CompoundNBT> getOutput(){
        return RNG.nextBoolean() ? Pair.of(outputState2, outputNbt2) : Pair.of(super.getOutputState(), super.getOutputNbt());
    }
}
