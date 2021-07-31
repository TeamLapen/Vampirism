package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.core.ModLoot;
import net.minecraft.entity.Entity;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;

import javax.annotation.Nonnull;

public class AdjustableLevelCondition implements ILootCondition {
    public static IBuilder builder(int level, LootContext.EntityTarget target) {
        return () -> new AdjustableLevelCondition(level, target);
    }
    private final int levelTest;
    private final LootContext.EntityTarget target;

    public AdjustableLevelCondition(int level, LootContext.EntityTarget targetIn) {
        levelTest = level;
        this.target = targetIn;
    }

    @Nonnull
    @Override
    public LootConditionType getType() {
        return ModLoot.adjustable_level;
    }

    @Override
    public boolean test(LootContext lootContext) {
        Entity e = lootContext.getParamOrNull(target.getParam());
        if (e instanceof IAdjustableLevel) {
            int l = ((IAdjustableLevel) e).getLevel();
            if (levelTest != -1) {
                return levelTest == l;
            }
        }
        return false;
    }

    public static class Serializer implements ILootSerializer<AdjustableLevelCondition> {


        @Nonnull
        @Override
        public AdjustableLevelCondition deserialize(JsonObject json, @Nonnull JsonDeserializationContext context) {
            return new AdjustableLevelCondition(json.has("level") ? JSONUtils.getAsInt(json, "level") : -1, JSONUtils.getAsObject(json, "entity", context, LootContext.EntityTarget.class));
        }

        @Override
        public void serialize(JsonObject json, AdjustableLevelCondition lootFunction, JsonSerializationContext context) {
            json.add("level", context.serialize(lootFunction.levelTest));
            json.add("entity", context.serialize(lootFunction.target));
        }

    }
}
