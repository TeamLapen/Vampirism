package de.teamlapen.vampirism.api.entity.player.vampire;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry for vampire skills.
 * Never use the Integer id's here, they are only intended to be used for sync and to update timers
 */
public class SkillRegistry {
    private static final BiMap<String, IVampireSkill> skillMap = HashBiMap.create();
    private static final BiMap<Integer, IVampireSkill> skillIdMap = HashBiMap.create();

    public static int getSkillCount() {
        return skillMap.size();
    }

    /**
     * @param player
     * @return A list of all skills the player can currently use
     */
    public static List<IVampireSkill> getAvailableSkills(IVampirePlayer player) {
        ArrayList<IVampireSkill> sl = new ArrayList<>();
        for (IVampireSkill s : skillMap.values()) {
            if (IVampireSkill.PERM.ALLOWED == s.canUse(player)) {
                sl.add(s);
            }
        }
        return sl;
    }

    /**
     * @param key
     * @return the skill that is registered with the given key
     */
    public static IVampireSkill getSkillFromKey(String key) {
        return skillMap.get(key);
    }

    /**
     * @param skill
     * @return the key which maps to the given skill
     */
    public static String getKeyFromSkill(IVampireSkill skill) {
        return skillMap.inverse().get(skill);
    }


    /**
     * FOR INTERNAL USAGE ONLY
     * Throws an exception if skill is not registered
     *
     * @param skill
     * @return The id currently mapped to this skill. Could be different after a restart.
     */
    public static int getIdFromSkill(IVampireSkill skill) {
        Integer i = skillIdMap.inverse().get(skill);
        if (i == null) {
            throw new SkillNotRegisteredException(skill);
        }
        return i;
    }

    /**
     * FOR INTERNAL USAGE ONLY
     *
     * @return The skill currently mapped to this id. Could be different after a restart
     */
    public static IVampireSkill getSkillFromId(int id) {
        return skillIdMap.get(id);
    }

    /**
     * Register a skill
     * Preferably during init
     *
     * @param skill
     * @return The same skill
     */
    public static <T extends IVampireSkill> T registerSkill(T skill, String key) {
        if (skillMap.put(key, skill) != null) {
            throw new IllegalArgumentException("There is already a skill registered for " + key);
        }
        skillIdMap.put(skillMap.size() - 1, skill);
        return skill;
    }

    /**
     * Is thrown if an unregistered skill is used
     */
    public static class SkillNotRegisteredException extends RuntimeException {
        public SkillNotRegisteredException(String name) {
            super("Skill " + name + " is not registed. You cannot use it otherwise");
        }

        public SkillNotRegisteredException(IVampireSkill skill) {
            this(skill.toString());
        }
    }
}
