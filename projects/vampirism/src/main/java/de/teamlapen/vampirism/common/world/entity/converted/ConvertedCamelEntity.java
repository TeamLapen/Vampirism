package de.teamlapen.vampirism.common.world.entity.converted;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class ConvertedCamelEntity extends ConvertedCamel {

    public ConvertedCamelEntity(EntityType<? extends ConvertedCamel> type, Level world) {
        super(type, world);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !isTamed();
    }
}
