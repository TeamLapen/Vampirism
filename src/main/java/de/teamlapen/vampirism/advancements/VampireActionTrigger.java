package de.teamlapen.vampirism.advancements;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Collection of several vampire related triggers
 */
public class VampireActionTrigger extends AbstractCriterionTrigger<VampireActionTrigger.Instance> {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "vampire_action");

    public VampireActionTrigger() {
        super(ID, Listeners::new);
    }

    @Nonnull
    @Override
    public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        Action action = Action.NONE;
        if (json.has("action")) {
            String name = json.get("action").getAsString();

            try {
                action = Action.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                VampirismMod.log.w("VampireActionTrigger", "Action %s does not exist", name);
            }
        } else {
            VampirismMod.log.w("VampireActionTrigger", "Action not specified");
        }
        return new Instance(action);
    }

    public void trigger(EntityPlayerMP player, Action action) {
        Listeners listeners = (Listeners) this.listenersForPlayers.get(player.getAdvancements());
        if (listeners != null) {
            listeners.trigger(action);
        }
    }

    public enum Action {
        BAT, SUCK_BLOOD, NONE
    }

    static class Instance extends AbstractCriterionInstance {
        private final @Nonnull
        Action action;

        Instance(@Nonnull Action action) {
            super(ID);
            this.action = action;
        }

        boolean trigger(Action action) {
            return this.action == action;
        }
    }

    static class Listeners extends GenericListeners<VampireActionTrigger.Instance> {

        Listeners(PlayerAdvancements playerAdvancementsIn) {
            super(playerAdvancementsIn);
        }

        void trigger(Action action) {
            List<Listener<Instance>> list = null;

            for (ICriterionTrigger.Listener<Instance> listener : this.playerListeners) {
                if ((listener.getCriterionInstance()).trigger(action)) {
                    if (list == null) {
                        list = Lists.newArrayList();
                    }

                    list.add(listener);
                }
            }

            if (list != null) {
                for (ICriterionTrigger.Listener<Instance> listener1 : list) {
                    listener1.grantCriterion(this.playerAdvancements);
                }
            }
        }
    }
}
