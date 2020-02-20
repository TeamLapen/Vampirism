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

    public static class Serializer extends ILootCondition.AbstractSerializer<TentSpawnerCondition> {

        public Serializer() {
            super(new ResourceLocation(REFERENCE.MODID, "is_tent_spawner"), TentSpawnerCondition.class);
        }

        @Override
        public TentSpawnerCondition deserialize(JsonObject json, JsonDeserializationContext context) {
            return INSTANCE;
        }

        @Override
        public void serialize(JsonObject json, TentSpawnerCondition value, JsonSerializationContext context) {

        }
    }
}
