package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.blockentity.TentBlockEntity;
import de.teamlapen.vampirism.core.ModLoot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder;

public class TentSpawnerCondition implements LootItemCondition {

    private final static TentSpawnerCondition INSTANCE = new TentSpawnerCondition();

    public static Builder builder() {
        return () -> INSTANCE;
    }

    @NotNull
    @Override
    public LootItemConditionType getType() {
        return ModLoot.is_tent_spawner.get();
    }

    @Override
    public boolean test(LootContext lootContext) {
        BlockEntity t = lootContext.getParamOrNull(LootContextParams.BLOCK_ENTITY);
        if (t instanceof TentBlockEntity) {
            return ((TentBlockEntity) t).isSpawner();
        }
        return false;
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<TentSpawnerCondition> {


        @NotNull
        @Override
        public TentSpawnerCondition deserialize(@NotNull JsonObject json, @NotNull JsonDeserializationContext context) {
            return INSTANCE;
        }

        @Override
        public void serialize(@NotNull JsonObject json, @NotNull TentSpawnerCondition value, @NotNull JsonSerializationContext context) {

        }


    }
}
