package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.api.items.IBloodChargeable;
import de.teamlapen.vampirism.core.ModLoot;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;

import javax.annotation.Nonnull;

/**
 * Function to set the charge of any {@link de.teamlapen.vampirism.api.items.IBloodChargeable}
 */
public class SetItemBloodCharge extends LootFunction {

    /**
     * In blood mB
     */
    private final IRandomRange charge;

    /**
     * Either charge or (minCharge and maxCharge) should be -1
     */
    private SetItemBloodCharge(ILootCondition[] conditions, IRandomRange charge) {
        super(conditions);
        this.charge = charge;
    }

    @Nonnull
    @Override
    public ItemStack doApply(@Nonnull ItemStack stack, @Nonnull LootContext context) {
        ((IBloodChargeable) stack.getItem()).charge(stack, charge.generateInt(context.getRandom()));
        return stack;
    }

    public static Builder<?> builder(IRandomRange p_215931_0_) {
        return builder((p_215930_1_) -> new SetItemBloodCharge(p_215930_1_, p_215931_0_));
    }

    @Override
    public LootFunctionType func_230425_b_() {
        return ModLoot.set_item_blood_charge;
    }

    public static class Serializer extends LootFunction.Serializer<SetItemBloodCharge> {
        @Override
        public void func_230424_a_(JsonObject object, SetItemBloodCharge p_230424_2_, JsonSerializationContext context) {
            super.func_230424_a_(object, p_230424_2_, context);
            object.add("charge", RandomRanges.serialize(p_230424_2_.charge, context));

        }


        @Nonnull
        @Override
        public SetItemBloodCharge deserialize(@Nonnull JsonObject jsonObject, @Nonnull JsonDeserializationContext jsonDeserializationContext, @Nonnull ILootCondition[] iLootConditions) {
            IRandomRange charge = RandomRanges.deserialize(jsonObject.get("charge"), jsonDeserializationContext);
            return new SetItemBloodCharge(iLootConditions, charge);
        }
    }
}
