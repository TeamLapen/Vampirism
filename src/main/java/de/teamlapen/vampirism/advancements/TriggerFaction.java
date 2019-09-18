package de.teamlapen.vampirism.advancements;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;

public class TriggerFaction extends AbstractCriterionTrigger<TriggerFaction.Instance> {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "faction");
    private final static Logger LOGGER = LogManager.getLogger();

    public TriggerFaction() {
        super(ID, Listeners::new);
    }

    @Override
    public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {

        IPlayableFaction faction = null;
        if (json.has("faction")) {
            ResourceLocation id = new ResourceLocation(json.get("faction").getAsString());
            IFaction faction1 = VampirismAPI.factionRegistry().getFactionByID(id);
            if (faction1 == null || !(faction1 instanceof IPlayableFaction)) {
                LOGGER.warn("Given faction name does not exist or is not a playable faction: {}", id);
            } else {
                faction = (IPlayableFaction) faction1;
            }

        }
        int level = json.has("level") ? json.get("level").getAsInt() : 1;
        return new Instance(faction, level);
    }

    @Override
    public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
        this.listenersForPlayers.remove(playerAdvancementsIn);
    }

    /**
     * Trigger this criterion
     */
    public void trigger(ServerPlayerEntity playerMP, IPlayableFaction faction, int level) {
        Listeners listeners = (Listeners) this.listenersForPlayers.get(playerMP.getAdvancements());
        if (listeners != null) {
            listeners.trigger(faction, level);
        }
    }

    static class Instance extends CriterionInstance {
        @Nullable
        private final IPlayableFaction faction;
        private final int level;

        public Instance(@Nullable IPlayableFaction faction, int level) {
            super(ID);
            this.faction = faction;
            this.level = level;
        }

        public boolean trigger(IPlayableFaction faction, int level) {
            if (this.faction == null || this.faction.equals(faction)) {
                return level >= this.level;
            }
            return false;
        }
    }

    static class Listeners extends GenericListeners<TriggerFaction.Instance> {

        Listeners(PlayerAdvancements playerAdvancementsIn) {
            super(playerAdvancementsIn);
        }

        void trigger(IPlayableFaction faction, int level) {
            List<Listener<Instance>> list = null;

            for (Listener<Instance> listener : this.playerListeners) {
                if ((listener.getCriterionInstance()).trigger(faction, level)) {
                    if (list == null) {
                        list = Lists.newArrayList();
                    }

                    list.add(listener);
                }
            }

            if (list != null) {
                for (Listener<Instance> listener1 : list) {
                    listener1.grantCriterion(this.playerAdvancements);
                }
            }
        }
    }
}
