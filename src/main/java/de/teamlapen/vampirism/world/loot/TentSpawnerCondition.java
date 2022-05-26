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

import javax.annotation.Nonnull;

public class TentSpawnerCondition implements LootItemCondition {

    private final static TentSpawnerCondition INSTANCE = new TentSpawnerCondition();

    public static Builder builder() {
        return () -> INSTANCE;
    }

    @Nonnull
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


        @Nonnull
        @Override
        public TentSpawnerCondition deserialize(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext context) {
            return INSTANCE;
        }

        @Override
        public void serialize(@Nonnull JsonObject json, @Nonnull TentSpawnerCondition value, @Nonnull JsonSerializationContext context) {

        }


    }
}
