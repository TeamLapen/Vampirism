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


public class StakeCondition implements ILootCondition {
    private final LootContext.EntityTarget target;

    public StakeCondition(LootContext.EntityTarget targetIn) {
        this.target = targetIn;
    }

    @Override
    public LootConditionType func_230419_b_() {
        return ModLoot.with_stake;
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

    public static class Serializer implements ILootSerializer<StakeCondition> {


        @Override
        public StakeCondition func_230423_a_(JsonObject json, JsonDeserializationContext context) {
            return new StakeCondition(JSONUtils.deserializeClass(json, "entity", context, LootContext.EntityTarget.class));
        }

        @Override
        public void func_230424_a_(JsonObject json, StakeCondition value, JsonSerializationContext context) {
            json.add("entity", context.serialize(value.target));

        }


    }
}
