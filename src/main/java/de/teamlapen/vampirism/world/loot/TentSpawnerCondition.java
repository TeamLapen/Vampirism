package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.core.ModLoot;
import de.teamlapen.vampirism.tileentity.TentTileEntity;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.tileentity.TileEntity;


public class TentSpawnerCondition implements ILootCondition {

    private final static TentSpawnerCondition INSTANCE = new TentSpawnerCondition();

    @Override
    public LootConditionType func_230419_b_() {
        return ModLoot.is_tent_spawner;
    }

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


    public static class Serializer implements ILootSerializer<TentSpawnerCondition> {


        @Override
        public TentSpawnerCondition func_230423_a_(JsonObject json, JsonDeserializationContext context) {
            return INSTANCE;
        }

        @Override
        public void func_230424_a_(JsonObject json, TentSpawnerCondition value, JsonSerializationContext context) {

        }


    }
}
