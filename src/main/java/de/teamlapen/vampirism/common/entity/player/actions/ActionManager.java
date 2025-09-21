package de.teamlapen.vampirism.common.entity.player.actions;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionManager;
import de.teamlapen.vampirism.common.core.ModRegistries;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;


public class ActionManager implements IActionManager {

    @Override
    public @NotNull List<Holder<IAction<?>>> getActionsForFaction(@NotNull Holder<? extends IPlayableFaction<?>> faction) {
        //noinspection RedundantCast,unchecked
        return ModRegistries.ACTIONS.listElements().filter(action -> IFaction.is(faction, action.value().factions())).map(action -> (Holder<IAction<?>>) (Object) action).collect(Collectors.toList());
    }
}
