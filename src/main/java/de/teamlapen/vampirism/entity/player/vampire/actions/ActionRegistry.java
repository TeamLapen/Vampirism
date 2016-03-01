package de.teamlapen.vampirism.entity.player.vampire.actions;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.teamlapen.vampirism.api.entity.player.vampire.IActionRegistry;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import net.minecraft.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class ActionRegistry implements IActionRegistry {
    private final BiMap<String, IVampireAction> actionMap = HashBiMap.create();
    private final BiMap<Integer, IVampireAction> actionIdMap = HashBiMap.create();

    @Override
    public int getActionCount() {
        return actionMap.size();
    }

    /**
     *
     * @return The skill currently mapped to this id. Could be different after a restart
     */
    public IVampireAction getActionFromId(int id) {
        return actionIdMap.get(id);
    }

    @Override
    public IVampireAction getActionFromKey(String key) {
        return actionMap.get(key);
    }

    @Override
    public List<IVampireAction> getAvailableActions(IVampirePlayer player) {
        ArrayList<IVampireAction> sl = new ArrayList<>();
        for (IVampireAction s : actionMap.values()) {
            if (IVampireAction.PERM.ALLOWED == s.canUse(player)) {
                sl.add(s);
            }
        }
        return sl;
    }

    /**
     * Throws an exception if skill is not registered
     *
     * @param skill
     * @return The id currently mapped to this skill. Could be different after a restart.
     */
    public int getIdFromAction(IVampireAction skill) {
        Integer i = actionIdMap.inverse().get(skill);
        if (i == null) {
            throw new ActionNotRegisteredException(skill);
        }
        return i;
    }

    @Override
    public String getKeyFromAction(IVampireAction action) {
        return actionMap.inverse().get(action);
    }

    @Override
    public <T extends IVampireAction> T registerAction(T skill, String key) {
        if (skill == null || StringUtils.isNullOrEmpty(key)) {
            throw new IllegalArgumentException(String.format("Tried to either register a null skill (%s) or with a null key (%s)", skill, key));
        }
        if (actionMap.put(key, skill) != null) {
            throw new IllegalArgumentException("There is already a skill registered for " + key);
        }
        actionIdMap.put(actionMap.size() - 1, skill);
        return skill;
    }

    /**
     * Is thrown if an unregistered skill is used
     */
    public class ActionNotRegisteredException extends RuntimeException {
        public ActionNotRegisteredException(String name) {
            super("Action " + name + " is not registed. You cannot use it otherwise");
        }

        public ActionNotRegisteredException(IVampireAction action) {
            this(action.toString());
        }
    }
}
