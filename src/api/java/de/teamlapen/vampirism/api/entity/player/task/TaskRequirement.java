package de.teamlapen.vampirism.api.entity.player.task;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;

public class TaskRequirement {

    private final Type type;

    public TaskRequirement(Type type) {
        this.type = type;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        KILLS, ITEMS
    }

    private static class Builder {

        private final ImmutableList.Builder<TaskRequirement> requirements = ImmutableList.builder();

        public Builder addEntityRequirement(EntityType<?> entityType, int amount) {
            this.requirements.add(new KillRequirement(entityType, amount));
            return this;
        }

        public Builder addItemRequirement(ItemStack itemStack) {
            this.requirements.add(new ItemRequirement(itemStack));
            return this;
        }

        public ImmutableList<TaskRequirement> build() {
            return requirements.build();
        }
    }
}
