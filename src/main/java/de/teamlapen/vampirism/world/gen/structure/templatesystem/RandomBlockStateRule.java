package de.teamlapen.vampirism.world.gen.structure.templatesystem;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.mixin.accessor.ProcessorRuleAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosAlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
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
    public static final Codec<Pair<BlockState, RuleBlockEntityModifier>> PAIR_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BlockState.CODEC.fieldOf("state").forGetter(entry -> {
            return entry.getLeft();
        }), RuleBlockEntityModifier.CODEC.optionalFieldOf("output_nbt", DEFAULT_BLOCK_ENTITY_MODIFIER).forGetter(entry -> {
            return entry.getValue();
        })).apply(instance, ImmutablePair::new);
    });
    @SuppressWarnings("CodeBlock2Expr")
    public static final Codec<RandomBlockStateRule> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(RuleTest.CODEC.fieldOf("input_predicate").forGetter((getter) -> {
            return ((ProcessorRuleAccessor) getter).getInputPredicate();
        }), RuleTest.CODEC.fieldOf("location_predicate").forGetter(entry -> {
            return ((ProcessorRuleAccessor) entry).getLocPredicate();
        }), PAIR_CODEC.fieldOf("default_state").forGetter(entry -> {
            return Pair.of(((ProcessorRuleAccessor) entry).getOutputState(), ((ProcessorRuleAccessor) entry).getBlockEntityModifier());
        }), PAIR_CODEC.listOf().fieldOf("states").forGetter(entry -> {
            return Lists.newArrayList(entry.states);
        })).apply(instance, RandomBlockStateRule::new);
    });
    private static final Random RNG = new Random();

    private final List<Pair<BlockState, RuleBlockEntityModifier>> states;

    public RandomBlockStateRule(@NotNull RuleTest inputPredicate, @NotNull RuleTest locationPredicate, BlockState defaultState, @NotNull List<BlockState> outputStates) {
        this(inputPredicate, locationPredicate, Pair.of(defaultState, DEFAULT_BLOCK_ENTITY_MODIFIER), outputStates.stream().map(state -> Pair.<BlockState, RuleBlockEntityModifier>of(state, DEFAULT_BLOCK_ENTITY_MODIFIER)).collect(Collectors.toList()));
    }

    public RandomBlockStateRule(@NotNull RuleTest inputPredicate, @NotNull RuleTest locationPredicate, @NotNull Pair<BlockState, RuleBlockEntityModifier> defaultState, List<Pair<BlockState, RuleBlockEntityModifier>> states) {
        super(inputPredicate, locationPredicate, PosAlwaysTrueTest.INSTANCE, defaultState.getLeft(), defaultState.getRight());
        this.states = states;
    }

    public Pair<BlockState, RuleBlockEntityModifier> getOutput() {
        if (!states.isEmpty()) {
            int type = RNG.nextInt(states.size());
            return states.get(type);
        } else {
            return Pair.of(((ProcessorRuleAccessor) this).getOutputState(), ((ProcessorRuleAccessor) this).getBlockEntityModifier());
        }
    }
}
