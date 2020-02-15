package de.teamlapen.vampirism.world.gen.util;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;

import de.teamlapen.vampirism.core.ModWorld;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.IDynamicDeserializer;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.template.AlwaysTrueRuleTest;
import net.minecraft.world.gen.feature.template.RuleEntry;
import net.minecraft.world.gen.feature.template.RuleTest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class RandomBlockState extends RuleEntry {
    private static final Random RNG = new Random();

    private final RuleTest inputPredicate;
    private final RuleTest locationPredicate;
    private final BlockState outputState2;
    @Nullable
    private final CompoundNBT outputNbt2;

    private boolean reCalculate = true;

    public RandomBlockState(RuleTest inputPredicate, RuleTest locationPredicate, BlockState outputState, BlockState outputState2) {
        this(inputPredicate, locationPredicate, outputState, outputState2, null, null);
    }

    public RandomBlockState(RuleTest inputPredicate, RuleTest locationPredicate, BlockState outputState, BlockState outputState2, @Nullable CompoundNBT outputNbt, @Nullable CompoundNBT outputNbt2) {
        super(inputPredicate, locationPredicate, outputState, outputNbt);
        this.outputState2 = outputState2;
        this.outputNbt2 = outputNbt2;
        this.inputPredicate = inputPredicate;
        this.locationPredicate = locationPredicate;
    }

    public Pair<BlockState, CompoundNBT> getOutput(){
        return RNG.nextBoolean()?Pair.of(outputState2,outputNbt2):Pair.of(super.getOutputState(), super.getOutputNbt());
    }

    @Nonnull
    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        T t = ops.createMap(ImmutableMap.of(ops.createString("input_predicate"), this.inputPredicate.serialize(ops).getValue(), ops.createString("location_predicate"), this.locationPredicate.serialize(ops).getValue(), ops.createString("output_state"), BlockState.serialize(ops, this.getOutputState()).getValue(), ops.createString("output_state_2"), BlockState.serialize(ops, this.outputState2).getValue()));
        if (this.getOutputNbt() == null && outputNbt2 == null) {
            return new Dynamic<>(ops, t);
        } else if (this.getOutputNbt() != null && outputNbt2 != null) {
            T t1 = ops.mergeInto(t, ops.createString("output_nbt"), (new Dynamic<>(NBTDynamicOps.INSTANCE, this.getOutputNbt())).convert(ops).getValue());
            return new Dynamic<>(ops, ops.mergeInto(t1, ops.createString("output_nbt_2"), (new Dynamic<>(NBTDynamicOps.INSTANCE, this.outputNbt2)).convert(ops).getValue()));
        } else if (outputNbt2 != null) {
            return new Dynamic<>(ops, ops.mergeInto(t, ops.createString("output_nbt_2"), (new Dynamic<>(NBTDynamicOps.INSTANCE, this.outputNbt2)).convert(ops).getValue()));
        }
        return new Dynamic<>(ops, ops.mergeInto(t, ops.createString("output_nbt"), new Dynamic<>(NBTDynamicOps.INSTANCE, this.getOutputNbt()).convert(ops).getValue()));
    }

    public static <T> RandomBlockState deserialize(Dynamic<T> dynamicIn) {
        Dynamic<T> inputDynamic = dynamicIn.get("input_predicate").orElseEmptyMap();
        Dynamic<T> locationDynamic = dynamicIn.get("location_predicate").orElseEmptyMap();
        RuleTest ruleTest = IDynamicDeserializer.func_214907_a(inputDynamic, Registry.RULE_TEST, "predicate_type", AlwaysTrueRuleTest.INSTANCE);
        RuleTest ruleTest1 = IDynamicDeserializer.func_214907_a(locationDynamic, Registry.RULE_TEST, "predicate_type", AlwaysTrueRuleTest.INSTANCE);
        BlockState blockState1 = BlockState.deserialize(dynamicIn.get("output_state").orElseEmptyMap());
        BlockState blockState2 = BlockState.deserialize(dynamicIn.get("output_state_2").orElseEmptyMap());
        CompoundNBT compoundNbt1 = (CompoundNBT) dynamicIn.get("output_nbt").map((dynamic) -> dynamic.convert(NBTDynamicOps.INSTANCE).getValue()).orElse(null);
        CompoundNBT compoundNbt2 = (CompoundNBT) dynamicIn.get("output_nbt_2").map((dynamic) -> dynamic.convert(NBTDynamicOps.INSTANCE).getValue()).orElse(null);
        return new RandomBlockState(ruleTest, ruleTest1, blockState1, blockState2, compoundNbt1, compoundNbt2);
    }
}
