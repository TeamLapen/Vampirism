package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.functions.ILootFunction;

public class SetMetaBasedOnLevel implements ILootFunction {
    private final int max;
    private final LootContext.EntityTarget target;

    protected SetMetaBasedOnLevel(int max, LootContext.EntityTarget targetIn) {
        this.max = max;
        this.target = targetIn;
    }

    @Override
    public ItemStack apply(ItemStack stack, LootContext context) {
        if (context.get(target.getParameter()) instanceof IAdjustableLevel) {
            int l = ((IAdjustableLevel) target.getParameter()).getLevel();
            if (max != -1) {
                l = Math.min(max, l);
            }
            stack.setDamage(l);
        }
        return stack;
    }

    public static class Serializer extends ILootFunction.Serializer<SetMetaBasedOnLevel> {

        protected Serializer() {
            super(new ResourceLocation(REFERENCE.MODID, "set_meta_from_level"), SetMetaBasedOnLevel.class);
        }

        @Override
        public SetMetaBasedOnLevel deserialize(JsonObject json, JsonDeserializationContext context) {
            return new SetMetaBasedOnLevel(json.has("max") ? JSONUtils.getInt(json, "max") : -1, JSONUtils.deserializeClass(json, "entity", context, LootContext.EntityTarget.class));
        }

        @Override
        public void serialize(JsonObject json, SetMetaBasedOnLevel value, JsonSerializationContext context) {
            if (value.max != -1) {
                json.addProperty("max", value.max);
            }
            json.add("entity", context.serialize(value.target));
        }
    }
}
