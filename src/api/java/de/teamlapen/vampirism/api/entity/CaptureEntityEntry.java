package de.teamlapen.vampirism.api.entity;

import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

import java.util.function.Supplier;

public class CaptureEntityEntry<T extends Mob> extends WeightedEntry.IntrusiveBase {

    private final Supplier<EntityType<T>> entity;

    public CaptureEntityEntry(Supplier<EntityType<T>> entity, int itemWeightIn) {
        super(itemWeightIn);
        this.entity = entity;
    }

    public EntityType<T> getEntity() {
        return entity.get();
    }
}
