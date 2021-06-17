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

    public static Builder<?> builder(IRandomRange p_215931_0_) {
        return builder((p_215930_1_) -> new SetItemBloodCharge(p_215930_1_, p_215931_0_));
    }
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

    @Nonnull
    @Override
    public LootFunctionType getFunctionType() {
        return ModLoot.set_item_blood_charge;
    }

    public static class Serializer extends LootFunction.Serializer<SetItemBloodCharge> {
        @Nonnull
        @Override
        public SetItemBloodCharge deserialize(@Nonnull JsonObject jsonObject, @Nonnull JsonDeserializationContext jsonDeserializationContext, @Nonnull ILootCondition[] iLootConditions) {
            IRandomRange charge = RandomRanges.deserialize(jsonObject.get("charge"), jsonDeserializationContext);
            return new SetItemBloodCharge(iLootConditions, charge);
        }

        @Override
        public void serialize(@Nonnull JsonObject object, @Nonnull SetItemBloodCharge lootFunction, @Nonnull JsonSerializationContext context) {
            super.serialize(object, lootFunction, context);
            object.add("charge", RandomRanges.serialize(lootFunction.charge, context));

        }
    }
}
