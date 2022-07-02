package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModLoot;
import de.teamlapen.vampirism.util.OilUtils;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import javax.annotation.Nonnull;

public class OilItemCondition implements LootItemCondition {

    private final IOil oil;

    public OilItemCondition(@Nonnull IOil oil) {
        this.oil = oil;
    }

    @Nonnull
    @Override
    public LootItemConditionType getType() {
        return ModLoot.with_oil_item.get();
    }

    @Override
    public boolean test(LootContext lootContext) {
        ItemStack stack = lootContext.getParamOrNull(LootContextParams.TOOL);
        return stack != null && OilUtils.getAppliedOil(stack).map(oil -> oil == this.oil).orElse(false);
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<OilItemCondition> {

        @Override
        public void serialize(@Nonnull JsonObject json, @Nonnull OilItemCondition condition, @Nonnull JsonSerializationContext context) {
            json.addProperty("oil", RegUtil.id(condition.oil).toString());
        }

        @Nonnull
        @Override
        public OilItemCondition deserialize(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext context) {
            ResourceLocation oil = new ResourceLocation(json.get("predicate").getAsJsonObject().get("oil").getAsString());
            return new OilItemCondition(RegUtil.getOil(oil));
        }
    }
}
