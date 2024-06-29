package de.teamlapen.vampirism.api.entity.player.actions;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.core.Holder;

import java.util.List;

/**
 * Helper for action registry related things.
 */
public interface IActionManager {

    List<Holder<IAction<?>>> getActionsForFaction(Holder<? extends IPlayableFaction<?>> faction);

}
