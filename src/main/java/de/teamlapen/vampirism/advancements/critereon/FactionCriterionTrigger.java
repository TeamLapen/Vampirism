package de.teamlapen.vampirism.advancements.critereon;

import com.google.gson.JsonObject;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.mixin.PlayerAdvancementsAccessor;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Predicate;

public class FactionCriterionTrigger extends SimpleCriterionTrigger<FactionCriterionTrigger.Instance> {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "faction");

    private final static Logger LOGGER = LogManager.getLogger();

    public static @NotNull Instance level(@Nullable IPlayableFaction<?> faction, int level) {
        return new Instance(Type.LEVEL, faction, level);
    }

    public static @NotNull Instance lord(@Nullable IPlayableFaction<?> faction, int lordLevel) {
        return new Instance(Type.LORD, faction, lordLevel);
    }

    @NotNull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    /**
     * Trigger this criterion
     */
    public void trigger(@NotNull ServerPlayer playerMP, IPlayableFaction<?> faction, int level, int lordLevel) {
        this.trigger(playerMP, (instance -> instance.test(faction, level, lordLevel)));
    }

    public void revokeAll(ServerPlayer player) {
        this.revoke(player, instance -> true);
    }

    public void revokeLevel(ServerPlayer player, IPlayableFaction<?> faction, Type type, int newLevel) {
        this.revoke(player, instance -> instance.faction == faction && instance.type == type && instance.level > newLevel);
    }

    private void revoke(ServerPlayer player, Predicate<Instance> instancePredicate) {
        PlayerAdvancements advancements = player.getAdvancements();
        ((PlayerAdvancementsAccessor) advancements).getAdvancements().entrySet().stream().filter(entry -> !entry.getValue().isDone()).forEach(advancementProgressEntry -> {
            if(advancementProgressEntry.getKey().getCriteria().values().stream().anyMatch(pair -> {
                CriterionTriggerInstance trigger = pair.getTrigger();
                return trigger != null && trigger.getCriterion().equals(FactionCriterionTrigger.ID) && instancePredicate.test(((Instance) trigger));
            })) {
                advancementProgressEntry.getValue().getCompletedCriteria().forEach(a -> advancements.revoke(advancementProgressEntry.getKey(), a));
            }
        });
    }

    @NotNull
    @Override
    protected Instance createInstance(@NotNull JsonObject json, @NotNull EntityPredicate.Composite entityPredicate, @NotNull DeserializationContext conditionsParser) {
        IPlayableFaction<?> playableFaction = null;
        Type type = json.has("type") ? Type.valueOf(json.get("type").getAsString()) : Type.LEVEL;
        if (json.has("faction")) {
            String idStr = json.get("faction").getAsString();
            if (!"null".equals(idStr)) {
                ResourceLocation id = new ResourceLocation(json.get("faction").getAsString());
                IFaction<?> faction = VampirismAPI.factionRegistry().getFactionByID(id);
                if (faction instanceof IPlayableFaction<?> playableFaction1) {
                    playableFaction = playableFaction1;
                } else {
                    LOGGER.warn("Given faction name does not exist or is not a playable faction: {}", id);
                }
            }
        }
        int level = json.has("level") ? json.get("level").getAsInt() : 1;
        return new Instance(type, playableFaction, level);
    }

    public enum Type {
        LEVEL, LORD
    }

    static class Instance extends AbstractCriterionTriggerInstance {

        @NotNull
        private final Type type;
        @Nullable
        private final IPlayableFaction<?> faction;
        private final int level;

        Instance(@NotNull Type type, @Nullable IPlayableFaction<?> faction, int level) {
            super(ID, EntityPredicate.Composite.ANY);
            this.type = type;
            this.faction = faction;
            this.level = level;
        }

        @NotNull
        @Override
        public JsonObject serializeToJson(@NotNull SerializationContext serializer) {
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
