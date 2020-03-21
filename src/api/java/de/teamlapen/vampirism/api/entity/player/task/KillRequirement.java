package de.teamlapen.vampirism.api.entity.player.task;

import net.minecraft.entity.EntityType;

public class KillRequirement extends TaskRequirement {

    private final EntityType<?> entityType;
    private final int amount;

    public KillRequirement(EntityType<?> entityType, int amount) {
        super(Type.KILLS);
        this.entityType = entityType;
        this.amount = amount;
    }

    public EntityType<?> getEntityType() {
        return entityType;
    }

    public int getAmount() {
        return amount;
    }
}