package de.teamlapen.vampirism.world.loot.conditions;

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
import org.jetbrains.annotations.NotNull;

public class OilItemCondition implements LootItemCondition {

    private final @NotNull IOil oil;

    public OilItemCondition(@NotNull IOil oil) {
        this.oil = oil;
    }

    @NotNull
    @Override
    public LootItemConditionType getType() {
        return ModLoot.with_oil_item.get();
    }

    @Override
    public boolean test(@NotNull LootContext lootContext) {
        ItemStack stack = lootContext.getParamOrNull(LootContextParams.TOOL);
        return stack != null && OilUtils.getAppliedOil(stack).map(oil -> oil == this.oil).orElse(false);
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<OilItemCondition> {

        @Override
        public void serialize(@NotNull JsonObject json, @NotNull OilItemCondition condition, @NotNull JsonSerializationContext context) {
            json.addProperty("oil", RegUtil.id(condition.oil).toString());
        }

        @NotNull
        @Override
        public OilItemCondition deserialize(@NotNull JsonObject json, @NotNull JsonDeserializationContext context) {
            ResourceLocation oil = new ResourceLocation(json.get("predicate").getAsJsonObject().get("oil").getAsString());
            return new OilItemCondition(RegUtil.getOil(oil));
        }
    }
}
