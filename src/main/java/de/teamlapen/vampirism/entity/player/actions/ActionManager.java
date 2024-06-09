package de.teamlapen.vampirism.entity.player.actions;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionManager;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;


public class ActionManager implements IActionManager {

    @SuppressWarnings("unchecked")
    @Override
    @Deprecated
    public <T extends IFactionPlayer<T>> @NotNull List<IAction<T>> getActionsForFaction(@NotNull IPlayableFaction<T> faction) {
        return RegUtil.values(ModRegistries.ACTIONS).stream().filter(action -> action.getFaction().map(f -> f == faction).orElse(true)).map(action -> (IAction<T>) action).collect(Collectors.toList());
    }

    @Override
    public @NotNull List<Holder<IAction<?>>> getActionsForFaction(@NotNull Holder<? extends IPlayableFaction<?>> faction) {
        //noinspection RedundantCast,unchecked
        return ModRegistries.ACTIONS.holders().filter(action -> IFaction.is(faction, action.value().factions())).map(action -> (Holder<IAction<?>>) (Object) action).collect(Collectors.toList());
    }

    @Deprecated
    @Override
    public Registry<IAction<?>> getRegistry() {
        return ModRegistries.ACTIONS;
    }
}
