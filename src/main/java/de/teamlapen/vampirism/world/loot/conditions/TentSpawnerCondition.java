package de.teamlapen.vampirism.world.loot.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.blockentity.TentBlockEntity;
import de.teamlapen.vampirism.core.ModLoot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

public class TentSpawnerCondition implements LootItemCondition {

    private final static TentSpawnerCondition INSTANCE = new TentSpawnerCondition();
    public static final MapCodec<TentSpawnerCondition> CODEC = MapCodec.unit(INSTANCE);

    public static @NotNull Builder builder() {
        return () -> INSTANCE;
    }

    @NotNull
    @Override
    public LootItemConditionType getType() {
        return ModLoot.IS_TENT_SPAWNER.get();
    }

    @Override
    public boolean test(@NotNull LootContext lootContext) {
        BlockEntity t = lootContext.getParamOrNull(LootContextParams.BLOCK_ENTITY);
        if (t instanceof TentBlockEntity) {
            return ((TentBlockEntity) t).isSpawner();
        }
        return false;
    }

}
