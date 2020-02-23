package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import de.teamlapen.vampirism.api.items.IBloodChargeable;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.IRandomRange;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.RandomRanges;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.functions.ILootFunction;
import net.minecraft.world.storage.loot.functions.SetDamage;

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

    public static class Serializer extends LootFunction.Serializer<SetItemBloodCharge> {

        public Serializer() {
            super(new ResourceLocation(REFERENCE.MODID, "set_item_blood_charge"), SetItemBloodCharge.class);
        }

        @Override
        public void serialize(@Nonnull JsonObject object, @Nonnull SetItemBloodCharge functionClazz, @Nonnull JsonSerializationContext serializationContext) {
            super.serialize(object, functionClazz, serializationContext);
            object.add("charge", RandomRanges.serialize(functionClazz.charge, serializationContext));
        }

        @Nonnull
        @Override
        public SetItemBloodCharge deserialize(@Nonnull JsonObject jsonObject, @Nonnull JsonDeserializationContext jsonDeserializationContext, @Nonnull ILootCondition[] iLootConditions) {
            IRandomRange charge = RandomRanges.deserialize(jsonObject.get("charge"), jsonDeserializationContext);
            return new SetItemBloodCharge(iLootConditions, charge);
        }
    }
}
