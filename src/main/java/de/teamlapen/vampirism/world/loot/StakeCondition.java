package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.items.ItemStake;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;

import java.util.Random;

public class StakeCondition implements LootCondition {
    @Override
    public boolean testCondition(Random rand, LootContext context) {
        Entity player = context.getKillerPlayer();
        if (player instanceof EntityPlayer) {
            ItemStack active = ((EntityPlayer) player).getHeldItemMainhand();
            if (!active.isEmpty() && active.getItem() instanceof ItemStake) {
                return true;
            }
        }
        return false;
    }

    public static class Serializer extends LootCondition.Serializer<StakeCondition> {

        protected Serializer() {
            super(new ResourceLocation(REFERENCE.MODID, "with_stake"), StakeCondition.class);
        }

        @Override
        public void serialize(JsonObject json, StakeCondition value, JsonSerializationContext context) {

        }

        @Override
        public StakeCondition deserialize(JsonObject json, JsonDeserializationContext context) {
            return new StakeCondition();
        }
    }
}
