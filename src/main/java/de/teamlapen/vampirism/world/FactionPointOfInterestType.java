package de.teamlapen.vampirism.world;

import net.minecraft.block.BlockState;
import net.minecraft.util.SoundEvent;
import net.minecraft.village.PointOfInterestType;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Predicate;

public class FactionPointOfInterestType extends PointOfInterestType {
    public FactionPointOfInterestType(String name, Set<BlockState> validStates, int maxFreeTickets, @Nullable SoundEvent workSound, Predicate<PointOfInterestType> p_i51553_5_, int p_i51553_6_) {
        super(name, validStates, maxFreeTickets, workSound, p_i51553_5_, p_i51553_6_);
    }

    public FactionPointOfInterestType(String name, Set<BlockState> validStates, int maxFreeTickets, @Nullable SoundEvent workSound, int p_i51554_5_) {
        super(name, validStates, maxFreeTickets, workSound, p_i51554_5_);
    }
}
