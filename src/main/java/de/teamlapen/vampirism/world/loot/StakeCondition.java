package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import de.teamlapen.vampirism.items.StakeItem;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

import java.util.Random;

public class StakeCondition implements ILootCondition {
    @Override
    public boolean testCondition(Random rand, LootContext context) {
        Entity player = context.getKillerPlayer();
        if (player instanceof PlayerEntity) {
            ItemStack active = ((PlayerEntity) player).getHeldItemMainhand();
            return !active.isEmpty() && active.getItem() instanceof StakeItem;
        }
        return false;
    }

    public static class Serializer extends ILootCondition.Serializer<StakeCondition> {

        protected Serializer() {
            super(new ResourceLocation(REFERENCE.MODID, "with_stake"), StakeCondition.class);
        }

        @Override
        public StakeCondition deserialize(JsonObject json, JsonDeserializationContext context) {
            return new StakeCondition();
        }

        @Override
        public void serialize(JsonObject json, StakeCondition value, JsonSerializationContext context) {

        }
    }
}
