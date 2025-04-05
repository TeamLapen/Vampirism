package de.teamlapen.vampirism.world.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.core.ModLoot;
import de.teamlapen.vampirism.items.component.BottleBlood;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class SetBloodFunction extends LootItemConditionalFunction {

    public static final MapCodec<SetBloodFunction> CODEC = RecordCodecBuilder.mapCodec(inst ->
            commonFields(inst)
                    .and(Codec.INT.fieldOf("minBlood").forGetter(function -> function.minBlood))
                    .and(Codec.INT.fieldOf("maxBlood").forGetter(function -> function.maxBlood))
                    .apply(inst, SetBloodFunction::new)
    );

    public static @NotNull Builder<?> builder(int minBlood, int maxBlood) {
        return simpleBuilder(conditions -> new SetBloodFunction(conditions, minBlood, maxBlood));
    }

    public static @NotNull Builder<?> builder(int blood) {
        return simpleBuilder(conditions -> new SetBloodFunction(conditions, blood, blood));
    }

    public final int minBlood;
    public final int maxBlood;

    protected SetBloodFunction(List<LootItemCondition> predicates, int minBlood, int maxBlood) {
        super(predicates);
        this.minBlood = minBlood;
        this.maxBlood = maxBlood;
    }

    @Override
    public @NotNull LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return ModLoot.SET_BLOOD.get();
    }

    @Override
    protected @NotNull ItemStack run(@NotNull ItemStack stack, @NotNull LootContext context) {
        stack.set(ModDataComponents.BOTTLE_BLOOD.get(), new BottleBlood(new Random().nextInt(minBlood, maxBlood + 1)));
        return stack;
    }
}
