package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.core.ModLoot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.loot.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import javax.annotation.Nonnull;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;
import net.minecraft.world.level.storage.loot.RandomIntGenerators;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public class SetMetaBasedOnLevel extends LootItemConditionalFunction {
    private final RandomIntGenerator max;
    private final LootContext.EntityTarget target;

    protected SetMetaBasedOnLevel(LootItemCondition[] conditions, RandomIntGenerator max, LootContext.EntityTarget targetIn) {
        super(conditions);
        this.max = max;
        this.target = targetIn;
    }

    @Nonnull
    @Override
    public LootItemFunctionType getType() {
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

    public static class Serializer extends LootItemConditionalFunction.Serializer<SetMetaBasedOnLevel> {

        @Nonnull
        @Override
        public SetMetaBasedOnLevel deserialize(@Nonnull JsonObject jsonObject, @Nonnull JsonDeserializationContext jsonDeserializationContext, @Nonnull LootItemCondition[] iLootConditions) {
            RandomIntGenerator range = RandomIntGenerators.deserialize(jsonObject.get("max"), jsonDeserializationContext);
            LootContext.EntityTarget target = jsonDeserializationContext.deserialize(jsonObject.get("entity"), LootContext.EntityTarget.class);
            return new SetMetaBasedOnLevel(iLootConditions, range, target);
        }

        @Override
        public void serialize(@Nonnull JsonObject json, @Nonnull SetMetaBasedOnLevel lootFunction, @Nonnull JsonSerializationContext context) {
            super.serialize(json, lootFunction, context);
            json.add("max", RandomIntGenerators.serialize(lootFunction.max, context));
            json.add("entity", context.serialize(lootFunction.target));
        }
    }
}
