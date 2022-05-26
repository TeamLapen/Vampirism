package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.core.ModLoot;
import de.teamlapen.vampirism.items.RefinementItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RefinementSetFunction extends LootItemConditionalFunction {

    public static Builder<?> builder(IFaction<?> faction) {
        return simpleBuilder(conditions -> new RefinementSetFunction(conditions, faction));
    }

    public static Builder<?> builder() {
        return simpleBuilder(conditions -> new RefinementSetFunction(conditions, null));
    }

    @Nullable
    public final IFaction<?> faction;

    public RefinementSetFunction(@Nonnull LootItemCondition[] conditionsIn, @Nullable IFaction<?> faction) {
        super(conditionsIn);
        this.faction = faction;
    }

    @Nonnull
    @Override
    public LootItemFunctionType getType() {
        return ModLoot.add_refinement_set.get();
    }

    @Nonnull
    @Override
    protected ItemStack run(@Nonnull ItemStack stack, @Nonnull LootContext context) {
        if (stack.getItem() instanceof IRefinementItem) {
            IRefinementSet set = RefinementItem.getRandomRefinementForItem(faction, ((IRefinementItem) stack.getItem()));
            if (set != null) {
                ((IRefinementItem) stack.getItem()).applyRefinementSet(stack, set);
            }
        }
        return stack;
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<RefinementSetFunction> {

        @Nonnull
        @Override
        public RefinementSetFunction deserialize(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext context, @Nonnull LootItemCondition[] conditionsIn) {
            IFaction<?> faction = null;
            if (json.has("faction")) {
                String string = json.get("faction").getAsString();
                faction = VampirismAPI.factionRegistry().getFactionByID(new ResourceLocation(json.get("faction").getAsString()));
                if (faction == null) {
                    throw new IllegalStateException("Faction " + string + " does not exist");
                }
            }
            return new RefinementSetFunction(conditionsIn, faction);
        }

        @Override
        public void serialize(@Nonnull JsonObject json, @Nonnull RefinementSetFunction function, @Nonnull JsonSerializationContext context) {
            super.serialize(json, function, context);
            if (function.faction != null) {
                json.addProperty("faction", function.faction.getID().toString());
            }
        }
    }
}
