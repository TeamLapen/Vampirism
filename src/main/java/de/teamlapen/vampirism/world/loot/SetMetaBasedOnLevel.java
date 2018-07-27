package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

import java.util.Random;

public class SetMetaBasedOnLevel extends LootFunction {
    private final int max;

    protected SetMetaBasedOnLevel(LootCondition[] conditionsIn, int max) {
        super(conditionsIn);
        this.max = max;
    }

    @Override
    public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
        Entity e = context.getLootedEntity();
        if (e instanceof IAdjustableLevel) {
            int l = ((IAdjustableLevel) e).getLevel();
            if (max != -1) {
                l = Math.min(max, l);
            }
            stack.setItemDamage(l);
        }
        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<SetMetaBasedOnLevel> {

        protected Serializer() {
            super(new ResourceLocation(REFERENCE.MODID, "set_meta_from_level"), SetMetaBasedOnLevel.class);
        }

        @Override
        public SetMetaBasedOnLevel deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn) {
            return new SetMetaBasedOnLevel(conditionsIn, object.has("max") ? JsonUtils.getInt(object, "max") : -1);
        }

        @Override
        public void serialize(JsonObject object, SetMetaBasedOnLevel functionClazz, JsonSerializationContext serializationContext) {
            if (functionClazz.max != -1) {
                object.addProperty("max", functionClazz.max);
            }
        }
    }
}
