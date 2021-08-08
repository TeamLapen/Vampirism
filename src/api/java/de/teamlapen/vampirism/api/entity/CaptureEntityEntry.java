package de.teamlapen.vampirism.api.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.util.WeighedRandom;

public class CaptureEntityEntry extends WeighedRandom.WeighedRandomItem {
    private final EntityType<? extends Mob> entity;

    public CaptureEntityEntry(EntityType<? extends Mob> entity, int itemWeightIn) {
        super(itemWeightIn);
        this.entity = entity;
    }

    public EntityType<? extends Mob> getEntity() {
        return entity;
    }
}
