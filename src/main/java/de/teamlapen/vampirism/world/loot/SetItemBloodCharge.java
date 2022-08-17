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
import org.jetbrains.annotations.NotNull;

/**
 * Function to set the charge of any {@link de.teamlapen.vampirism.api.items.IBloodChargeable}
 */
public class SetItemBloodCharge extends LootItemConditionalFunction {

    public static @NotNull Builder<?> builder(NumberProvider p_215931_0_) {
        return simpleBuilder((p_215930_1_) -> new SetItemBloodCharge(p_215930_1_, p_215931_0_));
    }

    /**
     * In blood mB
     */
    private final NumberProvider charge;

    /**
     * Either charge or (minCharge and maxCharge) should be -1
     */
    private SetItemBloodCharge(LootItemCondition @NotNull [] conditions, NumberProvider charge) {
        super(conditions);
        this.charge = charge;
    }

    @NotNull
    @Override
    public LootItemFunctionType getType() {
        return ModLoot.set_item_blood_charge.get();
    }

    @NotNull
    @Override
    public ItemStack run(@NotNull ItemStack stack, @NotNull LootContext context) {
        ((IBloodChargeable) stack.getItem()).charge(stack, charge.getInt(context));
        return stack;
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<SetItemBloodCharge> {
        @NotNull
        @Override
        public SetItemBloodCharge deserialize(@NotNull JsonObject jsonObject, @NotNull JsonDeserializationContext jsonDeserializationContext, @NotNull LootItemCondition[] iLootConditions) {
            NumberProvider charge = GsonHelper.getAsObject(jsonObject, "charge", jsonDeserializationContext, NumberProvider.class);
            return new SetItemBloodCharge(iLootConditions, charge);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull SetItemBloodCharge lootFunction, @NotNull JsonSerializationContext context) {
            super.serialize(object, lootFunction, context);
            object.add("charge", context.serialize(lootFunction.charge));

        }
    }
}
