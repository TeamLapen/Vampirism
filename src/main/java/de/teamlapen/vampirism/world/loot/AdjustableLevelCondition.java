package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.core.ModLoot;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

public class AdjustableLevelCondition implements LootItemCondition {
    public static @NotNull Builder builder(int level, LootContext.EntityTarget target) {
        return () -> new AdjustableLevelCondition(level, target);
    }

    private final int levelTest;
    private final LootContext.EntityTarget target;

    public AdjustableLevelCondition(int level, LootContext.EntityTarget targetIn) {
        levelTest = level;
        this.target = targetIn;
    }

    @NotNull
    @Override
    public LootItemConditionType getType() {
        return ModLoot.adjustable_level.get();
    }

    @Override
    public boolean test(@NotNull LootContext lootContext) {
        Entity e = lootContext.getParamOrNull(target.getParam());
        if (e instanceof IAdjustableLevel) {
            int l = ((IAdjustableLevel) e).getEntityLevel();
            if (levelTest != -1) {
                return levelTest == l;
            }
        }
        return false;
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<AdjustableLevelCondition> {


        @NotNull
        @Override
        public AdjustableLevelCondition deserialize(@NotNull JsonObject json, @NotNull JsonDeserializationContext context) {
            return new AdjustableLevelCondition(json.has("level") ? GsonHelper.getAsInt(json, "level") : -1, GsonHelper.getAsObject(json, "entity", context, LootContext.EntityTarget.class));
        }

        @Override
        public void serialize(@NotNull JsonObject json, @NotNull AdjustableLevelCondition lootFunction, @NotNull JsonSerializationContext context) {
            json.add("level", context.serialize(lootFunction.levelTest));
            json.add("entity", context.serialize(lootFunction.target));
        }

    }
}
