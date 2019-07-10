package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class AdjustableLevelCondition implements ILootCondition {
    private final int levelTest;
    private final LootContext.EntityTarget target;

    public AdjustableLevelCondition(int level, LootContext.EntityTarget targetIn) {
        levelTest = level;
        this.target = targetIn;
    }

    @Override
    public boolean test(LootContext lootContext) {
        if (lootContext.get(target.getParameter()) instanceof IAdjustableLevel) {
            int l = ((IAdjustableLevel) target.getParameter()).getLevel();
            if (levelTest != -1) {
                return levelTest == l;
            }
        }
        return false;
    }

    public static class Serializer extends ILootCondition.AbstractSerializer<AdjustableLevelCondition> {

        protected Serializer() {
            super(new ResourceLocation(REFERENCE.MODID, "adjustable_level"), AdjustableLevelCondition.class);
        }

        @Override
        public AdjustableLevelCondition deserialize(JsonObject json, JsonDeserializationContext context) {
            return new AdjustableLevelCondition(json.has("level") ? JSONUtils.getInt(json, "level") : -1, JSONUtils.deserializeClass(json, "entity", context, LootContext.EntityTarget.class));
        }

        @Override
        public void serialize(JsonObject json, AdjustableLevelCondition value, JsonSerializationContext context) {
            json.add("level", context.serialize(value.levelTest));
            json.add("entity", context.serialize(value.target));
        }
    }
}
