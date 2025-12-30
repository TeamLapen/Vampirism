package de.teamlapen.faction.api.world.entities.player;

import de.teamlapen.faction.api.Factions;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.NotNull;

public interface INeutralPlayer extends IFactionPlayer<INeutralPlayer> {

    @Override
    @NotNull
    default Holder<? extends IPlayableFaction<?>> getFaction() {
        return Factions.NEUTRAL;
    }

    @Override
    default boolean canLeaveFaction() {
        return true;
    }

    @Override
    default int getLevel() {
        return 0;
    }

    @Override
    default int getMaxLevel() {
        return 0;
    }

    @Override
    default boolean isDisguised() {
        return false;
    }

}
