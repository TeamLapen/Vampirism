package de.teamlapen.vampirism.data.loot.conditions;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.common.world.blockentity.TentBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class TentSpawnerCondition implements LootItemCondition {

    private final static TentSpawnerCondition INSTANCE = new TentSpawnerCondition();
    public static final MapCodec<TentSpawnerCondition> CODEC = MapCodec.unit(INSTANCE);

    public static @NotNull Builder builder() {
        return () -> INSTANCE;
    }

    @Override
    public @NonNull MapCodec<? extends LootItemCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull LootContext lootContext) {
        BlockEntity t = lootContext.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (t instanceof TentBlockEntity) {
            return ((TentBlockEntity) t).isSpawner();
        }
        return false;
    }

}
