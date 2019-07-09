package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.Entity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

import java.util.Random;

public class AdjustableLevelCondition implements ILootCondition {
    private final int levelTest;

    public AdjustableLevelCondition(int level) {
        levelTest = level;
    }

    @Override
    public boolean testCondition(Random random, LootContext lootContext) {
        Entity e = lootContext.getLootedEntity();
        if (e instanceof IAdjustableLevel) {
            int l = ((IAdjustableLevel) e).getLevel();
            if (levelTest != -1) {
                return levelTest == l;
            }
        }
        return false;
    }

    public static class Serializer extends ILootCondition.Serializer<AdjustableLevelCondition> {

        protected Serializer() {
            super(new ResourceLocation(REFERENCE.MODID, "adjustable_level"), AdjustableLevelCondition.class);
        }

        @Override
        public AdjustableLevelCondition deserialize(JsonObject json, JsonDeserializationContext context) {
            return new AdjustableLevelCondition(json.has("level") ? JSONUtils.getInt(json, "level") : -1);
        }

        @Override
        public void serialize(JsonObject json, AdjustableLevelCondition value, JsonSerializationContext context) {

        }
    }
}
