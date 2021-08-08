package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.api.items.IBloodChargeable;
import de.teamlapen.vampirism.core.ModLoot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.loot.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import javax.annotation.Nonnull;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;
import net.minecraft.world.level.storage.loot.RandomIntGenerators;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction.Builder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

/**
 * Function to set the charge of any {@link de.teamlapen.vampirism.api.items.IBloodChargeable}
 */
public class SetItemBloodCharge extends LootItemConditionalFunction {

    public static Builder<?> builder(RandomIntGenerator p_215931_0_) {
        return simpleBuilder((p_215930_1_) -> new SetItemBloodCharge(p_215930_1_, p_215931_0_));
    }
    /**
     * In blood mB
     */
    private final RandomIntGenerator charge;

    /**
     * Either charge or (minCharge and maxCharge) should be -1
     */
    private SetItemBloodCharge(LootItemCondition[] conditions, RandomIntGenerator charge) {
        super(conditions);
        this.charge = charge;
    }

    @Nonnull
    @Override
    public LootItemFunctionType getType() {
        return ModLoot.set_item_blood_charge;
    }

    @Nonnull
    @Override
    public ItemStack run(@Nonnull ItemStack stack, @Nonnull LootContext context) {
        ((IBloodChargeable) stack.getItem()).charge(stack, charge.getInt(context.getRandom()));
        return stack;
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<SetItemBloodCharge> {
        @Nonnull
        @Override
        public SetItemBloodCharge deserialize(@Nonnull JsonObject jsonObject, @Nonnull JsonDeserializationContext jsonDeserializationContext, @Nonnull LootItemCondition[] iLootConditions) {
            RandomIntGenerator charge = RandomIntGenerators.deserialize(jsonObject.get("charge"), jsonDeserializationContext);
            return new SetItemBloodCharge(iLootConditions, charge);
        }

        @Override
        public void serialize(@Nonnull JsonObject object, @Nonnull SetItemBloodCharge lootFunction, @Nonnull JsonSerializationContext context) {
            super.serialize(object, lootFunction, context);
            object.add("charge", RandomIntGenerators.serialize(lootFunction.charge, context));

        }
    }
}
