package de.teamlapen.vampirism.api.entity.player.neutral;

import de.teamlapen.vampirism.api.VampirismFactions;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.NotNull;

public interface INeutralPlayer extends IFactionPlayer<INeutralPlayer> {

    @Override
    @NotNull
    default Holder<? extends IPlayableFaction<?>> getFaction() {
        return VampirismFactions.NEUTRAL;
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

    @Override
    default void onLevelChanged(int newLevel, int oldLevel) {

    }
}
