package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.core.ModLoot;
import de.teamlapen.vampirism.items.StakeItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;

import javax.annotation.Nonnull;

public class StakeCondition implements ILootCondition {
    public static IBuilder builder(LootContext.EntityTarget target) {
        return () -> new StakeCondition(target);
    }
    private final LootContext.EntityTarget target;

    public StakeCondition(LootContext.EntityTarget targetIn) {
        this.target = targetIn;
    }

    @Nonnull
    @Override
    public LootConditionType getType() {
        return ModLoot.with_stake;
    }

    @Override
    public boolean test(LootContext context) {
        Entity player = context.getParamOrNull(target.getParam());
        if (player instanceof PlayerEntity) {
            ItemStack active = ((PlayerEntity) player).getMainHandItem();
            return !active.isEmpty() && active.getItem() instanceof StakeItem;
        }
        return false;
    }

    public static class Serializer implements ILootSerializer<StakeCondition> {


        @Nonnull
        @Override
        public StakeCondition deserialize(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext context) {
            return new StakeCondition(JSONUtils.getAsObject(json, "entity", context, LootContext.EntityTarget.class));
        }

        @Override
        public void serialize(JsonObject json, StakeCondition value, JsonSerializationContext context) {
            json.add("entity", context.serialize(value.target));

        }


    }
}
