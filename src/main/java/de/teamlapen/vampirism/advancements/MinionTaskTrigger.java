package de.teamlapen.vampirism.advancements;

import com.google.gson.JsonObject;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class MinionTaskTrigger extends AbstractCriterionTrigger<MinionTaskTrigger.Instance> {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "minion_tasks");

    public static Instance tasks(IMinionTask<?, ?> task) {
        return new Instance(task);
    }

    @Nonnull
    @Override
    protected Instance deserializeTrigger(@Nonnull JsonObject json, @Nonnull EntityPredicate.AndPredicate entityPredicate, @Nonnull ConditionArrayParser conditionsParser) {
        IMinionTask<?, ?> task = ModRegistries.MINION_TASKS.getValue(new ResourceLocation(json.get("action").getAsString()));
        if (task != null) {
            return new Instance(task);
        } else {
            throw new IllegalArgumentException("Could not deserialize minion trigger");
        }
    }

    public void trigger(ServerPlayerEntity player, IMinionTask<?,?> task) {
        this.triggerListeners(player, instance -> instance.test(task));
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    static class Instance extends CriterionInstance {
        @Nonnull
        private final IMinionTask<?,?> task;

        Instance(@Nonnull IMinionTask<?,?> task) {
            super(ID, EntityPredicate.AndPredicate.ANY_AND);
            this.task = task;
        }

        boolean test(IMinionTask<?,?> action) {
            return this.task == action;
        }

        @Nonnull
        @Override
        public JsonObject serialize(@Nonnull ConditionArraySerializer serializer) {
            JsonObject json = super.serialize(serializer);
            json.addProperty("action", task.getRegistryName().toString());
            return json;
        }
    }
}
