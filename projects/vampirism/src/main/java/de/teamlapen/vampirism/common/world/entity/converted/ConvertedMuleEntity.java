package de.teamlapen.vampirism.common.world.entity.converted;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * Handwritten subclass of the generated {@link ConvertedMule} base, adding increased xp reward,
 * tamed-aware behaviour, and boosted health on randomization.
 */
public class ConvertedMuleEntity extends ConvertedMule {

    public ConvertedMuleEntity(EntityType<? extends ConvertedMule> type, Level world) {
        super(type, world);
        this.xpReward = 2;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !isTamed();
    }
}
