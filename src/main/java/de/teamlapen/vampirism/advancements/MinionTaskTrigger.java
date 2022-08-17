package de.teamlapen.vampirism.advancements;

import com.google.gson.JsonObject;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class MinionTaskTrigger extends SimpleCriterionTrigger<MinionTaskTrigger.Instance> {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "minion_tasks");

    public static @NotNull Instance tasks(@NotNull IMinionTask<?, ?> task) {
        return new Instance(task);
    }

    @NotNull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void trigger(@NotNull ServerPlayer player, IMinionTask<?, ?> task) {
        this.trigger(player, instance -> instance.test(task));
    }

    @NotNull
    @Override
    protected Instance createInstance(@NotNull JsonObject json, @NotNull EntityPredicate.Composite entityPredicate, @NotNull DeserializationContext conditionsParser) {
        IMinionTask<?, ?> task = RegUtil.getMinionTask(new ResourceLocation(json.get("action").getAsString()));
        if (task != null) {
            return new Instance(task);
        } else {
            throw new IllegalArgumentException("Could not deserialize minion trigger");
        }
    }

    static class Instance extends AbstractCriterionTriggerInstance {
        @NotNull
        private final IMinionTask<?, ?> task;

        Instance(@NotNull IMinionTask<?, ?> task) {
            super(ID, EntityPredicate.Composite.ANY);
            this.task = task;
        }

        @NotNull
        @Override
        public JsonObject serializeToJson(@NotNull SerializationContext serializer) {
            JsonObject json = super.serializeToJson(serializer);
            json.addProperty("action", RegUtil.id(task).toString());
            return json;
        }

        boolean test(IMinionTask<?, ?> action) {
            return this.task == action;
        }
    }
}
