package de.teamlapen.factions.api.entities.player;

import de.teamlapen.factions.api.Factions;
import de.teamlapen.factions.api.factions.IPlayableFaction;
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
