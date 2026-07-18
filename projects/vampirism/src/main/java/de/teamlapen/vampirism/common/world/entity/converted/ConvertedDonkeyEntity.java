package de.teamlapen.vampirism.common.world.entity.converted;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class ConvertedDonkeyEntity extends ConvertedDonkey {

    public ConvertedDonkeyEntity(EntityType<? extends ConvertedDonkey> type, Level world) {
        super(type, world);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !isTamed();
    }
}
