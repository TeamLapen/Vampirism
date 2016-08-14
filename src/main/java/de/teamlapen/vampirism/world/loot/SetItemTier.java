package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

import java.util.Random;

/**
 * Function to set the tier of any {@link IItemWithTier}
 */
class SetItemTier extends LootFunction {

    private final IItemWithTier.TIER tier;

    private SetItemTier(LootCondition[] conditionsIn, IItemWithTier.TIER tier) {
        super(conditionsIn);
        this.tier = tier;
    }

    @Override
    public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
        return ((IItemWithTier) stack.getItem()).setTier(stack, tier);
    }

    public static class Serializer extends LootFunction.Serializer<SetItemTier> {

        protected Serializer() {
            super(new ResourceLocation(REFERENCE.MODID, "set_item_tier"), SetItemTier.class);
        }

        @Override
        public SetItemTier deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn) {
            if (!object.has("tier")) VampirismMod.log.t("%s", object);
            String name = JsonUtils.getString(object, "tier");
            IItemWithTier.TIER tier = IItemWithTier.TIER.NORMAL;
            for (IItemWithTier.TIER t : IItemWithTier.TIER.values()) {
                if (t.getName().equals(name)) {
                    tier = t;
                    break;
                }
            }
            return new SetItemTier(conditionsIn, tier);
        }

        @Override
        public void serialize(JsonObject object, SetItemTier functionClazz, JsonSerializationContext serializationContext) {
            object.addProperty("tier", functionClazz.tier.getName());
        }
    }
}
