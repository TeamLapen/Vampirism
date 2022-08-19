package de.teamlapen.vampirism.world.gen.structure.templatesystem;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosAlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * should only be used with {@link de.teamlapen.vampirism.world.gen.structure.templatesystem.RandomStructureProcessor}
 * <p>
 * returns a random blockstate with nbt data for the given {@link #inputPredicate} and {@link #locPredicate}
 *
 * @see ProcessorRule
 */
public class RandomBlockStateRule extends ProcessorRule {
    @SuppressWarnings({"Convert2MethodRef", "CodeBlock2Expr"})
    public static final Codec<Pair<BlockState, Optional<CompoundTag>>> PAIR_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BlockState.CODEC.fieldOf("state").forGetter(entry -> {
            return entry.getLeft();
        }), CompoundTag.CODEC.optionalFieldOf("output_nbt").forGetter(entry -> {
            return entry.getValue();
        })).apply(instance, ImmutablePair::new);
    });
    @SuppressWarnings("CodeBlock2Expr")
    public static final Codec<RandomBlockStateRule> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(RuleTest.CODEC.fieldOf("input_predicate").forGetter((getter) -> {
            return getter.inputPredicate;
        }), RuleTest.CODEC.fieldOf("location_predicate").forGetter(entry -> {
            return entry.locPredicate;
        }), PAIR_CODEC.fieldOf("default_state").forGetter(entry -> {
            return Pair.of(entry.outputState, Optional.ofNullable(entry.outputTag));
        }), PAIR_CODEC.listOf().fieldOf("states").forGetter(entry -> {
            return Lists.newArrayList(entry.states);
        })).apply(instance, RandomBlockStateRule::new);
    });
    private static final Random RNG = new Random();

    private final List<Pair<BlockState, Optional<CompoundTag>>> states;

    public RandomBlockStateRule(@NotNull RuleTest inputPredicate, @NotNull RuleTest locationPredicate, BlockState defaultState, @NotNull List<BlockState> outputStates) {
        this(inputPredicate, locationPredicate, Pair.of(defaultState, Optional.empty()), outputStates.stream().map(state -> Pair.of(state, Optional.<CompoundTag>empty())).collect(Collectors.toList()));
    }

    public RandomBlockStateRule(@NotNull RuleTest inputPredicate, @NotNull RuleTest locationPredicate, @NotNull Pair<BlockState, Optional<CompoundTag>> defaultState, List<Pair<BlockState, Optional<CompoundTag>>> states) {
        super(inputPredicate, locationPredicate, PosAlwaysTrueTest.INSTANCE, defaultState.getLeft(), defaultState.getRight());
        this.states = states;
    }

    public Pair<BlockState, Optional<CompoundTag>> getOutput() {
        if (!states.isEmpty()) {
            int type = RNG.nextInt(states.size());
            return states.get(type);
        } else {
            return Pair.of(this.outputState, Optional.ofNullable(outputTag));
        }
    }
}
