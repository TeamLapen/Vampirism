package de.teamlapen.vampirism.advancements.critereon;

import com.google.gson.JsonObject;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Collection of several vampire related triggers
 */
public class VampireActionCriterionTrigger extends SimpleCriterionTrigger<VampireActionCriterionTrigger.Instance> {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "vampire_action");
    private static final Logger LOGGER = LogManager.getLogger();

    public static @NotNull Instance builder(@NotNull Action action) {
        return new Instance(action);
    }

    @NotNull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void trigger(@NotNull ServerPlayer player, Action action) {
        this.trigger(player, (instance) -> instance.test(action));
    }

    @NotNull
    @Override
    protected Instance createInstance(@NotNull JsonObject json, @NotNull ContextAwarePredicate entityPredicate, @NotNull DeserializationContext conditionsParser) {
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

    static class Instance extends AbstractCriterionTriggerInstance {
        @NotNull
        private final Action action;

        Instance(@NotNull Action action) {
            super(ID, ContextAwarePredicate.ANY);
            this.action = action;
        }

        @NotNull
        @Override
        public JsonObject serializeToJson(@NotNull SerializationContext serializer) {
            JsonObject json = super.serializeToJson(serializer);
            json.addProperty("action", action.name());
            return json;
        }

        boolean test(Action action) {
            return this.action == action;
        }
    }
}
