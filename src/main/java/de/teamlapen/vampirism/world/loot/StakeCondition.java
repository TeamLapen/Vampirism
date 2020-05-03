package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.items.StakeItem;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class StakeCondition implements ILootCondition {
    private final LootContext.EntityTarget target;

    public StakeCondition(LootContext.EntityTarget targetIn) {
        this.target = targetIn;
    }

    @Override
    public boolean test(LootContext context) {
        Entity player = context.get(target.getParameter());
        if (player instanceof PlayerEntity) {
            ItemStack active = ((PlayerEntity) player).getHeldItemMainhand();
            return !active.isEmpty() && active.getItem() instanceof StakeItem;
        }
        return false;
    }

    public static IBuilder builder(LootContext.EntityTarget target) {
        return () -> new StakeCondition(target);
    }

    public static class Serializer extends ILootCondition.AbstractSerializer<StakeCondition> {

        public Serializer() {
            super(new ResourceLocation(REFERENCE.MODID, "with_stake"), StakeCondition.class);
        }

        @Override
        public StakeCondition deserialize(JsonObject json, JsonDeserializationContext context) {
            return new StakeCondition(JSONUtils.deserializeClass(json, "entity", context, LootContext.EntityTarget.class));
        }

        @Override
        public void serialize(JsonObject json, StakeCondition value, JsonSerializationContext context) {
            json.add("entity", context.serialize(value.target));
        }
    }
}
