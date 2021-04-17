package de.teamlapen.vampirism.advancements;

import com.google.gson.JsonObject;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TriggerFaction extends AbstractCriterionTrigger<TriggerFaction.Instance> {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "faction");

    private final static Logger LOGGER = LogManager.getLogger();

    public static Instance builder(@Nullable IPlayableFaction<?> faction, int level){
        return new Instance(faction, level);
    }

    @Nonnull
    @Override
    protected Instance deserializeTrigger(JsonObject json, @Nonnull EntityPredicate.AndPredicate entityPredicate, @Nonnull ConditionArrayParser conditionsParser) {
        IPlayableFaction<?> faction = null;
        if (json.has("faction")) {
            ResourceLocation id = new ResourceLocation(json.get("faction").getAsString());
            IFaction<?> faction1 = VampirismAPI.factionRegistry().getFactionByID(id);
            if (!(faction1 instanceof IPlayableFaction)) {
                LOGGER.warn("Given faction name does not exist or is not a playable faction: {}", id);
            } else {
                faction = (IPlayableFaction<?>) faction1;
            }

        }
        int level = json.has("level") ? json.get("level").getAsInt() : 1;
        return new Instance(faction, level);
    }

    /**
     * Trigger this criterion
     */
    public void trigger(ServerPlayerEntity playerMP, IPlayableFaction<?> faction, int level) {
        this.triggerListeners(playerMP, (instance -> {
            return instance.test(faction, level);
        }));
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    static class Instance extends CriterionInstance {
        @Nullable
        private final IPlayableFaction<?> faction;
        private final int level;

        Instance(@Nullable IPlayableFaction<?> faction, int level) {
            super(ID, EntityPredicate.AndPredicate.ANY_AND); //TODO check what AndPredicate does
            this.faction = faction;
            this.level = level;
        }

        public boolean test(IPlayableFaction<?> faction, int level) {
            if (this.faction == null || this.faction.equals(faction)) {
                return level >= this.level;
            }
            return false;
        }

        @Nonnull
        @Override
        public JsonObject serialize(@Nonnull ConditionArraySerializer serializer) {
            JsonObject json = super.serialize(serializer);
            json.addProperty("faction", faction == null ? "null" : faction.getID().toString());
            json.addProperty("level", level);
            return json;
        }

    }
}
