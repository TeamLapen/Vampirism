package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.core.ModLoot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nonnull;

import net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder;

public class AdjustableLevelCondition implements LootItemCondition {
    public static Builder builder(int level, LootContext.EntityTarget target) {
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
    public LootItemConditionType getType() {
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

    public static class Serializer implements Serializer<AdjustableLevelCondition> {


        @Nonnull
        @Override
        public AdjustableLevelCondition deserialize(JsonObject json, @Nonnull JsonDeserializationContext context) {
            return new AdjustableLevelCondition(json.has("level") ? GsonHelper.getAsInt(json, "level") : -1, GsonHelper.getAsObject(json, "entity", context, LootContext.EntityTarget.class));
        }

        @Override
        public void serialize(JsonObject json, AdjustableLevelCondition lootFunction, JsonSerializationContext context) {
            json.add("level", context.serialize(lootFunction.levelTest));
            json.add("entity", context.serialize(lootFunction.target));
        }

    }
}
