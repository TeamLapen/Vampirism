package de.teamlapen.vampirism.player.actions;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionManager;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;


public class ActionManager implements IActionManager {

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IFactionPlayer<T>> @NotNull List<IAction<T>> getActionsForFaction(IPlayableFaction<T> faction) {
        return RegUtil.values(ModRegistries.ACTIONS).stream().filter(action -> action.getFaction().map(f -> f == faction).orElse(true)).map(action -> (IAction<T>) action).collect(Collectors.toList());
    }

    @Override
    public IForgeRegistry<IAction<?>> getRegistry() {
        return ModRegistries.ACTIONS.get();
    }
}
