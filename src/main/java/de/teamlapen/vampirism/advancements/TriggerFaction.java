package de.teamlapen.vampirism.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TriggerFaction implements ICriterionTrigger<TriggerFaction.Instance> {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "faction");
    private final Map<PlayerAdvancements, TriggerFaction.Listeners> listeners = Maps.newHashMap();

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void addListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listener) {
        Listeners listeners = this.listeners.get(playerAdvancementsIn);
        if (listeners == null) {
            listeners = new TriggerFaction.Listeners(playerAdvancementsIn);
            this.listeners.put(playerAdvancementsIn, listeners);
        }
        listeners.add(listener);
    }

    @Override
    public void removeListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listener) {
        Listeners listeners = this.listeners.get(playerAdvancementsIn);
        if (listeners != null) {
            listeners.remove(listener);
            if (listeners.isEmpty()) {
                this.listeners.remove(playerAdvancementsIn);
            }
        }
    }

    /**
     * Trigger this criterion
     */
    public void trigger(EntityPlayerMP playerMP, IPlayableFaction faction, int level) {
        Listeners listeners = this.listeners.get(playerMP.getAdvancements());
        if (listeners != null) {
            listeners.trigger(faction, level);
        }
    }


    @Override
    public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
        this.listeners.remove(playerAdvancementsIn);
    }

    @Override
    public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {

        IPlayableFaction faction = null;
        if (json.has("faction")) {
            String name = json.get("faction").getAsString();
            if (!"any".equals(name)) {
                IFaction faction1 = VampirismAPI.factionRegistry().getFactionByName(name);
                if (faction1 == null || !(faction1 instanceof IPlayableFaction)) {
                    VampirismMod.log.w("TriggerFaction", "Given faction name does not exist or is not a playable faction: %s", name);
                } else {
                    faction = (IPlayableFaction) faction1;
                }
            }
        }
        int level = json.has("level") ? json.get("level").getAsInt() : 1;
        return new Instance(faction, level);
    }


    static class Instance extends AbstractCriterionInstance {
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

    static class Listeners {
        private final PlayerAdvancements playerAdvancements;
        private final Set<Listener<TriggerFaction.Instance>> listeners = Sets.newHashSet();

        public Listeners(PlayerAdvancements playerAdvancementsIn) {
            this.playerAdvancements = playerAdvancementsIn;
        }

        public boolean isEmpty() {
            return this.listeners.isEmpty();
        }

        public void add(ICriterionTrigger.Listener<TriggerFaction.Instance> listener) {
            this.listeners.add(listener);
        }

        public void remove(ICriterionTrigger.Listener<TriggerFaction.Instance> listener) {
            this.listeners.remove(listener);
        }

        public void trigger(IPlayableFaction faction, int level) {
            List<Listener<TriggerFaction.Instance>> list = null;

            for (ICriterionTrigger.Listener<TriggerFaction.Instance> listener : this.listeners) {
                if ((listener.getCriterionInstance()).trigger(faction, level)) {
                    if (list == null) {
                        list = Lists.newArrayList();
                    }

                    list.add(listener);
                }
            }

            if (list != null) {
                for (ICriterionTrigger.Listener<TriggerFaction.Instance> listener1 : list) {
                    listener1.grantCriterion(this.playerAdvancements);
                }
            }
        }
    }
}
