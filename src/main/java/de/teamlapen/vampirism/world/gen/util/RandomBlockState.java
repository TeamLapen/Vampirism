package de.teamlapen.vampirism.world.gen.util;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.gen.feature.template.AlwaysTrueTest;
import net.minecraft.world.gen.feature.template.RuleEntry;
import net.minecraft.world.gen.feature.template.RuleTest;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * should only be used with {@link RandomStructureProcessor}
 * <p>
 * returns a random blockstate with nbt data for the given {@link #inputPredicate} and {@link #locationPredicate}
 *
 * @see RuleEntry
 */
public class RandomBlockState extends RuleEntry {
    @SuppressWarnings("Convert2MethodRef")
    public static final Codec<Pair<BlockState, Optional<CompoundNBT>>> PAIR_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BlockState.CODEC.fieldOf("state").forGetter(entry -> {
            return entry.getLeft();
        }), CompoundNBT.CODEC.optionalFieldOf("output_nbt").forGetter(entry -> {
            return entry.getValue();
        })).apply(instance, ImmutablePair::new);
    });
    @SuppressWarnings("CodeBlock2Expr")
    public static final Codec<RandomBlockState> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(RuleTest.field_237127_c_.fieldOf("input_predicate").forGetter((getter) -> {
            return getter.inputPredicate;
        }), RuleTest.field_237127_c_.fieldOf("location_predicate").forGetter(entry -> {
            return entry.locationPredicate;
        }), PAIR_CODEC.fieldOf("default_state").forGetter(entry -> {
            return Pair.of(entry.outputState, Optional.ofNullable(entry.outputNbt));
        }), PAIR_CODEC.listOf().fieldOf("states").forGetter(entry -> {
            return Lists.newArrayList(entry.states);
        })).apply(instance, RandomBlockState::new);
    });
    private static final Random RNG = new Random();

    private final List<Pair<BlockState, Optional<CompoundNBT>>> states;

    public RandomBlockState(RuleTest inputPredicate, RuleTest locationPredicate, BlockState defaultState, List<BlockState> outputStates) {
        this(inputPredicate, locationPredicate, Pair.of(defaultState, Optional.empty()), outputStates.stream().map(state -> Pair.of(state, Optional.<CompoundNBT>empty())).collect(Collectors.toList()));
    }

    public RandomBlockState(RuleTest inputPredicate, RuleTest locationPredicate, Pair<BlockState, Optional<CompoundNBT>> defaultState, List<Pair<BlockState, Optional<CompoundNBT>>> states) {
        super(inputPredicate, locationPredicate, AlwaysTrueTest.field_237100_b_, defaultState.getLeft(), defaultState.getRight());
        this.states = states;
    }

    public Pair<BlockState, Optional<CompoundNBT>> getOutput() {
        if (!states.isEmpty()) {
            int type = RNG.nextInt(states.size());
            return states.get(type);
        } else {
            return Pair.of(this.outputState, Optional.ofNullable(outputNbt));
        }
    }
}
