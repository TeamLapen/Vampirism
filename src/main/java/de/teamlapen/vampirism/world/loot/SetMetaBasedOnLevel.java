package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.core.ModLoot;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;

import javax.annotation.Nonnull;

public class SetMetaBasedOnLevel extends LootFunction {
    private final IRandomRange max;
    private final LootContext.EntityTarget target;

    protected SetMetaBasedOnLevel(ILootCondition[] conditions, IRandomRange max, LootContext.EntityTarget targetIn) {
        super(conditions);
        this.max = max;
        this.target = targetIn;
    }

    @Nonnull
    @Override
    public LootFunctionType getType() {
        return ModLoot.set_meta_from_level;
    }

    @Nonnull
    @Override
    public ItemStack run(@Nonnull ItemStack stack, LootContext context) {
        if (context.getParamOrNull(target.getParam()) instanceof IAdjustableLevel) {
            int l = ((IAdjustableLevel) target.getParam()).getLevel();
            int amount = max.getInt(context.getRandom());
            if (amount != -1)
                l = Math.min(amount, l);
            stack.setDamageValue(l);
        }
        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<SetMetaBasedOnLevel> {

        @Nonnull
        @Override
        public SetMetaBasedOnLevel deserialize(@Nonnull JsonObject jsonObject, @Nonnull JsonDeserializationContext jsonDeserializationContext, @Nonnull ILootCondition[] iLootConditions) {
            IRandomRange range = RandomRanges.deserialize(jsonObject.get("max"), jsonDeserializationContext);
            LootContext.EntityTarget target = jsonDeserializationContext.deserialize(jsonObject.get("entity"), LootContext.EntityTarget.class);
            return new SetMetaBasedOnLevel(iLootConditions, range, target);
        }

        @Override
        public void serialize(@Nonnull JsonObject json, @Nonnull SetMetaBasedOnLevel lootFunction, @Nonnull JsonSerializationContext context) {
            super.serialize(json, lootFunction, context);
            json.add("max", RandomRanges.serialize(lootFunction.max, context));
            json.add("entity", context.serialize(lootFunction.target));
        }
    }
}
