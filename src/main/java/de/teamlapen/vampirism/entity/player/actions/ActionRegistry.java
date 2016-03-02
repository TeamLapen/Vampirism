package de.teamlapen.vampirism.entity.player.actions;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IAction;
import de.teamlapen.vampirism.api.entity.player.IActionRegistry;
import net.minecraft.util.StringUtils;

import java.util.HashMap;
import java.util.Map;


public class ActionRegistry implements IActionRegistry {
    private final Map<IPlayableFaction, ImmutableBiMap<String, IAction>> actionMap = new HashMap<>();
    private Map<IPlayableFaction, ImmutableBiMap.Builder<String, IAction>> actionBuilder = new HashMap<>();

    /**
     * Call after all factions and actions are registered.
     * Builds action maps
     */
    public void finish() {
        if (actionBuilder == null) {
            throw new IllegalArgumentException("Action registry was already finished");
        }
        for (IPlayableFaction faction : VampirismAPI.factionRegistry().getPlayableFactions()) {
            if (actionBuilder.containsKey(faction)) {
                actionMap.put(faction, actionBuilder.get(faction).build());
            } else {
                actionMap.put(faction, ImmutableBiMap.copyOf(HashBiMap.<String, IAction>create()));
            }
        }
        actionBuilder = null;
    }

    @Override
    public int getActionCount(IPlayableFaction faction) {
        return actionMap.get(faction).size();
    }

    @Override
    public IAction getActionFromKey(IPlayableFaction faction, String key) {
        return actionMap.get(faction).get(key);
    }

    public ImmutableBiMap<String, IAction> getActionMapForFaction(IPlayableFaction faction) {
        return actionMap.get(faction);
    }

    @Override
    public String getKeyFromAction(IAction action) {
        IPlayableFaction faction = action.getFaction();
        return actionMap.get(faction).inverse().get(action);

    }

    @Override
    public <T extends IAction> T registerAction(T action, String key) {
        if (action == null || StringUtils.isNullOrEmpty(key)) {
            throw new IllegalArgumentException(String.format("Tried to either register a null action (%s) or with a null key (%s)", action, key));
        }
        IPlayableFaction faction = action.getFaction();
        if (!actionBuilder.containsKey(faction)) {
            actionBuilder.put(faction, ImmutableBiMap.<String, IAction>builder());
        }
        actionBuilder.get(faction).put(key, action);

        return action;
    }


}
