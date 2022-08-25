package de.teamlapen.vampirism.advancements;

import com.google.gson.JsonObject;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.mixin.PlayerAdvancementsAccessor;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.PlayerAdvancements;
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
import java.util.Objects;
import java.util.function.Predicate;

public class TriggerFaction extends AbstractCriterionTrigger<TriggerFaction.Instance> {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "faction");

    private final static Logger LOGGER = LogManager.getLogger();

    public static Instance builder(@Nullable IPlayableFaction<?> faction, int level) { //TODO 1.17 rename level
        return new Instance(Type.LEVEL, faction, level);
    }

    public static Instance lord(@Nullable IPlayableFaction<?> faction, int lordLevel) {
        return new Instance(Type.LORD, faction, lordLevel);
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    /**
     * Trigger this criterion
     */
    public void trigger(ServerPlayerEntity playerMP, IPlayableFaction<?> faction, int level, int lordLevel) {
        this.trigger(playerMP, (instance -> {
            return instance.test(faction, level, lordLevel);
        }));
    }

    public void revokeAll(ServerPlayerEntity player) {
        this.revoke(player, instance -> true);
    }

    public void revokeLevel(ServerPlayerEntity player, IPlayableFaction<?> faction, Type type, int newLevel) {
        this.revoke(player, instance -> instance.faction == faction && instance.type == type && instance.level > newLevel);
    }

    private void revoke(ServerPlayerEntity player, Predicate<Instance> instancePredicate) {
        PlayerAdvancements advancements = player.getAdvancements();
        ((PlayerAdvancementsAccessor) advancements).getAdvancements().entrySet().stream().filter(entry -> !entry.getValue().isDone()).forEach(advancementProgressEntry -> {
            if(advancementProgressEntry.getKey().getCriteria().values().stream().anyMatch(pair -> {
                ICriterionInstance trigger = pair.getTrigger();
                return trigger != null && trigger.getCriterion().equals(TriggerFaction.ID) && instancePredicate.test(((Instance) trigger));
            })) {
                advancementProgressEntry.getValue().getCompletedCriteria().forEach(a -> advancements.revoke(advancementProgressEntry.getKey(), a));
            }
        });
    }

    @Nonnull
    @Override
    protected Instance createInstance(JsonObject json, @Nonnull EntityPredicate.AndPredicate entityPredicate, @Nonnull ConditionArrayParser conditionsParser) {
        IPlayableFaction<?> faction = null;
        Type type = json.has("type") ? Type.valueOf(json.get("type").getAsString()) : Type.LEVEL;
        if (json.has("faction")) {
            String idStr = json.get("faction").getAsString();
            if (!"null".equals(idStr)) {
                ResourceLocation id = new ResourceLocation(json.get("faction").getAsString());
                IFaction<?> faction1 = VampirismAPI.factionRegistry().getFactionByID(id);
                if (!(faction1 instanceof IPlayableFaction)) {
                    LOGGER.warn("Given faction name does not exist or is not a playable faction: {}", id);
                } else {
                    faction = (IPlayableFaction<?>) faction1;
                }
            }
        }
        int level = json.has("level") ? json.get("level").getAsInt() : 1;
        return new Instance(type, faction, level);
    }

    public enum Type {
        LEVEL, LORD
    }

    static class Instance extends CriterionInstance {

        @Nonnull
        private final Type type;
        @Nullable
        private final IPlayableFaction<?> faction;
        private final int level;

        Instance(@Nonnull Type type, @Nullable IPlayableFaction<?> faction, int level) {
            super(ID, EntityPredicate.AndPredicate.ANY); //TODO check what AndPredicate does
            this.type = type;
            this.faction = faction;
            this.level = level;
        }

        @Nonnull
        @Override
        public JsonObject serializeToJson(@Nonnull ConditionArraySerializer serializer) {
            JsonObject json = super.serializeToJson(serializer);
            json.addProperty("type", type.name());
            json.addProperty("faction", faction == null ? "null" : faction.getID().toString());
            json.addProperty("level", level);
            return json;
        }

        public boolean test(IPlayableFaction<?> faction, int level, int lordLevel) {
            if ((faction == null && this.faction == null) || Objects.equals(this.faction, faction)) {
                if (type == Type.LEVEL) {
                    return level >= this.level;
                } else if (type == Type.LORD) {
                    return lordLevel >= this.level;
                }
            }
            return false;
        }

    }
}
