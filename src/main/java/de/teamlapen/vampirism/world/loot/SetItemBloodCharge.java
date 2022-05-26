package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.api.items.IBloodChargeable;
import de.teamlapen.vampirism.core.ModLoot;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import javax.annotation.Nonnull;

/**
 * Function to set the charge of any {@link de.teamlapen.vampirism.api.items.IBloodChargeable}
 */
public class SetItemBloodCharge extends LootItemConditionalFunction {

    public static Builder<?> builder(NumberProvider p_215931_0_) {
        return simpleBuilder((p_215930_1_) -> new SetItemBloodCharge(p_215930_1_, p_215931_0_));
    }

    /**
     * In blood mB
     */
    private final NumberProvider charge;

    /**
     * Either charge or (minCharge and maxCharge) should be -1
     */
    private SetItemBloodCharge(LootItemCondition[] conditions, NumberProvider charge) {
        super(conditions);
        this.charge = charge;
    }

    @Nonnull
    @Override
    public LootItemFunctionType getType() {
        return ModLoot.set_item_blood_charge.get();
    }

    @Nonnull
    @Override
    public ItemStack run(@Nonnull ItemStack stack, @Nonnull LootContext context) {
        ((IBloodChargeable) stack.getItem()).charge(stack, charge.getInt(context));
        return stack;
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<SetItemBloodCharge> {
        @Nonnull
        @Override
        public SetItemBloodCharge deserialize(@Nonnull JsonObject jsonObject, @Nonnull JsonDeserializationContext jsonDeserializationContext, @Nonnull LootItemCondition[] iLootConditions) {
            NumberProvider charge = GsonHelper.getAsObject(jsonObject, "charge", jsonDeserializationContext, NumberProvider.class);
            return new SetItemBloodCharge(iLootConditions, charge);
        }

        @Override
        public void serialize(@Nonnull JsonObject object, @Nonnull SetItemBloodCharge lootFunction, @Nonnull JsonSerializationContext context) {
            super.serialize(object, lootFunction, context);
            object.add("charge", context.serialize(lootFunction.charge));

        }
    }
}
