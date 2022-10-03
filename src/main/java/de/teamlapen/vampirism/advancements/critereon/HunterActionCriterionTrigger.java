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
 * Collection of several hunter related triggers
 */
public class HunterActionCriterionTrigger extends SimpleCriterionTrigger<HunterActionCriterionTrigger.Instance> {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "hunter_action");
    private final static Logger LOGGER = LogManager.getLogger();

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
    protected Instance createInstance(@NotNull JsonObject json, @NotNull EntityPredicate.Composite entityPredicate, @NotNull DeserializationContext conditionsParser) {
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
        STAKE, NONE
    }

    static class Instance extends AbstractCriterionTriggerInstance {
        @NotNull
        private final Action action;

        Instance(@NotNull Action action) {
            super(ID, EntityPredicate.Composite.ANY);
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
