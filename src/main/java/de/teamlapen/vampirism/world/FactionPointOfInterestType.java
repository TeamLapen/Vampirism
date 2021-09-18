package de.teamlapen.vampirism.world;

import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.function.Predicate;

public class FactionPointOfInterestType extends PoiType {
    public FactionPointOfInterestType(String name, Set<BlockState> validStates, int maxFreeTickets, @Nonnull Predicate<PoiType> p_i51553_5_, int p_i51553_6_) {
        super(name, validStates, maxFreeTickets, p_i51553_5_, p_i51553_6_);
    }

    public FactionPointOfInterestType(String name, Set<BlockState> validStates, int maxFreeTickets, int p_i51554_5_) {
        super(name, validStates, maxFreeTickets, p_i51554_5_);
    }
}
