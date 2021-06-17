package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.core.ModLoot;
import de.teamlapen.vampirism.items.VampireRefinementItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RefinementSetFunction extends LootFunction {

    public static Builder<?> builder(IFaction<?> faction) {
        return builder(conditions -> new RefinementSetFunction(conditions, faction));
    }

    public static Builder<?> builder() {
        return builder(conditions -> new RefinementSetFunction(conditions, null));
    }
    @Nullable
    public final IFaction<?> faction;

    public RefinementSetFunction(@Nonnull ILootCondition[] conditionsIn, @Nullable IFaction<?> faction) {
        super(conditionsIn);
        this.faction = faction;
    }

    @Nonnull
    @Override
    public LootFunctionType getFunctionType() {
        return ModLoot.add_refinement_set;
    }

    @Nonnull
    @Override
    protected ItemStack doApply(@Nonnull ItemStack stack, @Nonnull LootContext context) {
        if (stack.getItem() instanceof VampireRefinementItem) {
            IRefinementSet set = VampireRefinementItem.getRandomRefinementForItem(faction, ((VampireRefinementItem) stack.getItem()));
            if (set != null) {
                ((VampireRefinementItem) stack.getItem()).applyRefinementSet(stack, set);
            }
        }
        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<RefinementSetFunction> {

        @Nonnull
        @Override
        public RefinementSetFunction deserialize(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext context, @Nonnull ILootCondition[] conditionsIn) {
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
