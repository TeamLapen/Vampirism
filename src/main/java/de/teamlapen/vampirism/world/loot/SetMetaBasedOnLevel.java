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
    public ItemStack doApply(@Nonnull ItemStack stack, LootContext context) {
        if (context.get(target.getParameter()) instanceof IAdjustableLevel) {
            int l = ((IAdjustableLevel) target.getParameter()).getLevel();
            int amount = max.generateInt(context.getRandom());
            if (amount != -1)
                l = Math.min(amount, l);
            stack.setDamage(l);
        }
        return stack;
    }

    @Nonnull
    @Override
    public LootFunctionType getFunctionType() {
        return ModLoot.set_meta_from_level;
    }

    public static class Serializer extends LootFunction.Serializer<SetMetaBasedOnLevel> {

        @Override
        public void serialize(@Nonnull JsonObject json, @Nonnull SetMetaBasedOnLevel lootFunction, @Nonnull JsonSerializationContext context) {
            super.serialize(json, lootFunction, context);
            json.add("max", RandomRanges.serialize(lootFunction.max, context));
            json.add("entity", context.serialize(lootFunction.target));
        }


        @Nonnull
        @Override
        public SetMetaBasedOnLevel deserialize(@Nonnull JsonObject jsonObject, @Nonnull JsonDeserializationContext jsonDeserializationContext, @Nonnull ILootCondition[] iLootConditions) {
            IRandomRange range = RandomRanges.deserialize(jsonObject.get("max"), jsonDeserializationContext);
            LootContext.EntityTarget target = jsonDeserializationContext.deserialize(jsonObject.get("entity"), LootContext.EntityTarget.class);
            return new SetMetaBasedOnLevel(iLootConditions, range, target);
        }
    }
}
