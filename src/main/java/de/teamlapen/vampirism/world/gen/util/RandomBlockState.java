package de.teamlapen.vampirism.world.gen.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.IDynamicDeserializer;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.template.AlwaysTrueRuleTest;
import net.minecraft.world.gen.feature.template.RuleEntry;
import net.minecraft.world.gen.feature.template.RuleTest;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomBlockState extends RuleEntry {
    private static final Random RNG = new Random();

    private final RuleTest inputPredicate;
    private final RuleTest locationPredicate;
    private final Pair<BlockState,CompoundNBT>[] outputStates;

    public RandomBlockState(RuleTest inputPredicate, RuleTest locationPredicate, BlockState... outputStates) {
        this(inputPredicate, locationPredicate, Arrays.stream(outputStates).map(state -> Pair.of(state,(CompoundNBT)null)).collect(Collectors.toList()).toArray(new Pair[]{}));
    }

    public RandomBlockState(RuleTest inputPredicate, RuleTest locationPredicate, Pair<BlockState,CompoundNBT>... outputStates) {
        super(inputPredicate, locationPredicate, outputStates[0].getKey(), outputStates[0].getValue());
        this.outputStates = outputStates;
        this.inputPredicate = inputPredicate;
        this.locationPredicate = locationPredicate;
    }

    public Pair<BlockState, CompoundNBT> getOutput(){
        return outputStates[RNG.nextInt(outputStates.length)];
    }

    @Nonnull
    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        ImmutableMap.Builder<T,T> builder = ImmutableMap.builder();
        builder.put(ops.createString("input_predicate"), this.inputPredicate.serialize(ops).getValue());
        builder.put(ops.createString("location_predicate"), this.locationPredicate.serialize(ops).getValue());
        builder.put(ops.createString("amount"), ops.createInt(this.outputStates.length));
        for (int i = 0; i < outputStates.length; i++) {
            builder.put(ops.createString("output_state_"+i),BlockState.serialize(ops, outputStates[i].getKey()).getValue());
            if(outputStates[i].getValue() != null) {
                builder.put(ops.createString("output_nbt_"+i), (new Dynamic<>(NBTDynamicOps.INSTANCE, outputStates[i].getValue())).convert(ops).getValue());
            }
        }
        T t = ops.createMap(builder.build());
        return new Dynamic<>(ops,t);
    }

    public static <T> RandomBlockState deserialize(Dynamic<T> dynamicIn) {
        Dynamic<T> inputDynamic = dynamicIn.get("input_predicate").orElseEmptyMap();
        Dynamic<T> locationDynamic = dynamicIn.get("location_predicate").orElseEmptyMap();
        RuleTest ruleTest = IDynamicDeserializer.func_214907_a(inputDynamic, Registry.RULE_TEST, "predicate_type", AlwaysTrueRuleTest.INSTANCE);
        RuleTest ruleTest1 = IDynamicDeserializer.func_214907_a(locationDynamic, Registry.RULE_TEST, "predicate_type", AlwaysTrueRuleTest.INSTANCE);
        List<Pair<BlockState,CompoundNBT>> list = Lists.newArrayList();
        for(int i = 0; i < dynamicIn.get("amount").asInt(0);i++) {
            BlockState blockState = BlockState.deserialize(dynamicIn.get("output_state_"+i).orElseEmptyMap());
            CompoundNBT compoundNbt = (CompoundNBT) dynamicIn.get("output_nbt_"+i).map((dynamic) -> dynamic.convert(NBTDynamicOps.INSTANCE).getValue()).orElse(null);
            list.add(Pair.of(blockState,compoundNbt));
        }
        return new RandomBlockState(ruleTest, ruleTest1, list.toArray(new Pair[]{}));
    }
}
