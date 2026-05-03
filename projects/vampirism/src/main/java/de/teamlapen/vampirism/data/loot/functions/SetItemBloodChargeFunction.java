package de.teamlapen.vampirism.data.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.world.items.IBloodChargeable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Function to set the charge of any {@link IBloodChargeable}
 */
public class SetItemBloodChargeFunction extends LootItemConditionalFunction {

    public static final MapCodec<SetItemBloodChargeFunction> CODEC = RecordCodecBuilder.mapCodec(inst ->
            commonFields(inst)
                    .and(NumberProviders.CODEC.fieldOf("charge").forGetter(l -> l.charge))
                    .apply(inst, SetItemBloodChargeFunction::new));

    public static @NotNull Builder<?> builder(NumberProvider p_215931_0_) {
        return simpleBuilder((p_215930_1_) -> new SetItemBloodChargeFunction(p_215930_1_, p_215931_0_));
    }

    /**
     * In blood mB
     */
    private final NumberProvider charge;

    /**
     * Either charge or (minCharge and maxCharge) should be -1
     */
    private SetItemBloodChargeFunction(@NotNull List<LootItemCondition> conditions, NumberProvider charge) {
        super(conditions);
        this.charge = charge;
    }

    @Override
    public MapCodec<? extends LootItemConditionalFunction> codec() {
        return CODEC;
    }

    @NotNull
    @Override
    public ItemStack run(@NotNull ItemStack stack, @NotNull LootContext context) {
        ((IBloodChargeable) stack.getItem()).charge(stack, charge.getInt(context));
        return stack;
    }
}
