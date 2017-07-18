package de.teamlapen.vampirism.player.actions;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionManager;
import de.teamlapen.vampirism.core.VampirismRegistries;

import java.util.List;

/**
 * 1.12
 *
 * @author maxanier
 */
public class ActionManager implements IActionManager {

    @Override
    public List<IAction> getActionsForFaction(IPlayableFaction faction) {
        List<IAction> list = Lists.newArrayList(VampirismRegistries.ACTIONS.getValues());
        list.removeIf(action -> !faction.equals(action.getFaction()));
        return list;
    }
}
