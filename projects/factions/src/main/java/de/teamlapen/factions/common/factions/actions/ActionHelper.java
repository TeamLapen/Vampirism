package de.teamlapen.factions.common.factions.actions;

import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.actions.IAction;
import de.teamlapen.factions.common.core.ModRegistries;
import net.minecraft.core.Holder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ActionHelper {

    private static final Map<Holder<? extends IFaction<?>>, List<Holder<IAction<?>>>> actions = new ConcurrentHashMap<>();

    public static List<Holder<IAction<?>>> getActions(Holder<? extends IFaction<?>> factionHolder) {
        return actions.computeIfAbsent(factionHolder, k -> ModRegistries.ACTIONS.listElements().filter(f -> IFaction.is(factionHolder, f.value().factions())).collect(Collectors.toUnmodifiableList()));
    }
}
