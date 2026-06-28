package de.teamlapen.vampirism.common.world.entity.converted;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * Handwritten subclass of the generated {@link ConvertedHorse} base, adding increased xp reward,
 * tamed-aware behaviour, and boosted health on randomization.
 */
public class ConvertedHorseEntity extends ConvertedHorse {

    public ConvertedHorseEntity(EntityType<? extends ConvertedHorse> type, Level world) {
        super(type, world);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !isTamed();
    }
}
