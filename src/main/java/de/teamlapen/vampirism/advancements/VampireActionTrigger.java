package de.teamlapen.vampirism.advancements;

import com.google.gson.JsonObject;
import de.teamlapen.vampirism.REFERENCE;
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

/**
 * Collection of several vampire related triggers
 */
public class VampireActionTrigger extends AbstractCriterionTrigger<VampireActionTrigger.Instance> {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "vampire_action");
    private static final Logger LOGGER = LogManager.getLogger();

    public static Instance builder(Action action) {
        return new Instance(action);
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, Action action) {
        this.trigger(player, (instance) -> {
            return instance.test(action);
        });
    }

    @Nonnull
    @Override
    protected Instance createInstance(JsonObject json, @Nonnull EntityPredicate.AndPredicate entityPredicate, @Nonnull ConditionArrayParser conditionsParser) {
        Action action = Action.NONE;
        if (json.has("action")) {
            String name = json.get("action").getAsString();

            try {
                action = Action.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Action {} does not exist", name);
            }
        } else {
            LOGGER.warn("Action not specified");
        }
        return new Instance(action);
    }

    public enum Action {
        SNIPED_IN_BAT, POISONOUS_BITE, PERFORM_RITUAL_INFUSION, BAT, SUCK_BLOOD, NONE, KILL_FROZEN_HUNTER
    }

    static class Instance extends CriterionInstance {
        @Nonnull
        private final Action action;

        Instance(@Nonnull Action action) {
            super(ID, EntityPredicate.AndPredicate.ANY);
            this.action = action;
        }

        @Nonnull
        @Override
        public JsonObject serializeToJson(@Nonnull ConditionArraySerializer serializer) {
            JsonObject json = super.serializeToJson(serializer);
            json.addProperty("action", action.name());
            return json;
        }

        boolean test(Action action) {
            return this.action == action;
        }
    }
}
