package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.ConstantRange;
import net.minecraft.world.storage.loot.IRandomRange;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.RandomRanges;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.functions.ILootFunction;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;

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

    public static class Serializer extends LootFunction.Serializer<SetMetaBasedOnLevel> {

        public Serializer() {
            super(new ResourceLocation(REFERENCE.MODID, "set_meta_from_level"), SetMetaBasedOnLevel.class);
        }

        @Override
        public void serialize(@Nonnull JsonObject json, SetMetaBasedOnLevel value, @Nonnull JsonSerializationContext context) {
            super.serialize(json, value, context);
            json.add("max", RandomRanges.serialize(value.max, context));
            json.add("entity", context.serialize(value.target));
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
