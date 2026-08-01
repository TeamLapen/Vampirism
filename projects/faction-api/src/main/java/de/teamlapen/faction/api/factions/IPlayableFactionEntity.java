package de.teamlapen.faction.api.factions;

import de.teamlapen.faction.api.world.entities.extensions.IPlayer;
import net.minecraft.core.Holder;

public interface IPlayableFactionEntity extends IFactionEntity, IPlayer {
    @Override
    Holder<? extends IPlayableFaction<?>> getFaction();
}
