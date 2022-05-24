package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModLoot;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.util.OilUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class OilItemCondition implements ILootCondition {

    private final IOil oil;

    public OilItemCondition(@Nonnull IOil oil) {
        this.oil = oil;
    }

    @Nonnull
    @Override
    public LootConditionType getType() {
        return ModLoot.with_oil_item;
    }

    @Override
    public boolean test(LootContext lootContext) {
        ItemStack stack = lootContext.getParamOrNull(LootParameters.TOOL);
        return stack != null && OilUtils.getAppliedOil(stack).map(oil -> oil == this.oil).orElse(false);
    }

    public static class Serializer implements ILootSerializer<OilItemCondition> {

        @Override
        public void serialize(@Nonnull JsonObject json, @Nonnull OilItemCondition condition, @Nonnull JsonSerializationContext context) {
            json.addProperty("oil", condition.oil.getRegistryName().toString());
        }

        @Nonnull
        @Override
        public OilItemCondition deserialize(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext context) {
            ResourceLocation oil = new ResourceLocation(json.get("predicate").getAsJsonObject().get("oil").getAsString());
            //noinspection ConstantConditions
            return new OilItemCondition(ModRegistries.OILS.getValue(oil));
        }
    }
}
