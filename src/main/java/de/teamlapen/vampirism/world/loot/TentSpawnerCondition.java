package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.tileentity.TentTileEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

import javax.annotation.Nonnull;


public class TentSpawnerCondition implements ILootCondition {

    private final static TentSpawnerCondition INSTANCE = new TentSpawnerCondition();

    @Override
    public boolean test(LootContext lootContext) {
        TileEntity t = lootContext.get(LootParameters.BLOCK_ENTITY);
        if (t instanceof TentTileEntity) {
            return ((TentTileEntity) t).isSpawner();
        }
        return false;
    }

    public static IBuilder builder() {
        return () -> INSTANCE;
    }

    public static class Serializer extends ILootCondition.AbstractSerializer<TentSpawnerCondition> {

        public Serializer() {
            super(new ResourceLocation(REFERENCE.MODID, "is_tent_spawner"), TentSpawnerCondition.class);
        }

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
