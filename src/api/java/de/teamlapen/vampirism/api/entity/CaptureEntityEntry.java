package de.teamlapen.vampirism.api.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.WeightedRandom;

public class CaptureEntityEntry extends WeightedRandom.Item {
    private final EntityType<? extends MobEntity> entity;

    public CaptureEntityEntry(EntityType<? extends MobEntity> entity, int itemWeightIn) {
        super(itemWeightIn);
        this.entity = entity;
    }

    public EntityType<? extends MobEntity> getEntity() {
        return entity;
    }
}
