package de.teamlapen.vampirism.api.entity;

import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

public class CaptureEntityEntry extends WeightedEntry.IntrusiveBase {
    private final EntityType<? extends Mob> entity;

    public CaptureEntityEntry(EntityType<? extends Mob> entity, int itemWeightIn) {
        super(itemWeightIn);
        this.entity = entity;
    }

    public EntityType<? extends Mob> getEntity() {
        return entity;
    }
}
