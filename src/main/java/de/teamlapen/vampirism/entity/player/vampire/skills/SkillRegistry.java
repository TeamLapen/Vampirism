package de.teamlapen.vampirism.entity.player.vampire.skills;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.teamlapen.vampirism.api.entity.player.vampire.ISkillRegistry;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireSkill;
import net.minecraft.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class SkillRegistry implements ISkillRegistry {
    private final BiMap<String, IVampireSkill> skillMap = HashBiMap.create();
    private final BiMap<Integer, IVampireSkill> skillIdMap = HashBiMap.create();

    @Override
    public List<IVampireSkill> getAvailableSkills(IVampirePlayer player) {
        ArrayList<IVampireSkill> sl = new ArrayList<>();
        for (IVampireSkill s : skillMap.values()) {
            if (IVampireSkill.PERM.ALLOWED == s.canUse(player)) {
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
    public int getIdFromSkill(IVampireSkill skill) {
        Integer i = skillIdMap.inverse().get(skill);
        if (i == null) {
            throw new SkillNotRegisteredException(skill);
        }
        return i;
    }

    @Override
    public String getKeyFromSkill(IVampireSkill skill) {
        return skillMap.inverse().get(skill);
    }

    @Override
    public int getSkillCount() {
        return skillMap.size();
    }

    /**
     *
     * @return The skill currently mapped to this id. Could be different after a restart
     */
    public IVampireSkill getSkillFromId(int id) {
        return skillIdMap.get(id);
    }

    @Override
    public IVampireSkill getSkillFromKey(String key) {
        return skillMap.get(key);
    }

    @Override
    public <T extends IVampireSkill> T registerSkill(T skill, String key) {
        if (skill == null || StringUtils.isNullOrEmpty(key)) {
            throw new IllegalArgumentException(String.format("Tried to either register a null skill (%s) or with a null key (%s)", skill, key));
        }
        if (skillMap.put(key, skill) != null) {
            throw new IllegalArgumentException("There is already a skill registered for " + key);
        }
        skillIdMap.put(skillMap.size() - 1, skill);
        return skill;
    }

    /**
     * Is thrown if an unregistered skill is used
     */
    public class SkillNotRegisteredException extends RuntimeException {
        public SkillNotRegisteredException(String name) {
            super("Skill " + name + " is not registed. You cannot use it otherwise");
        }

        public SkillNotRegisteredException(IVampireSkill skill) {
            this(skill.toString());
        }
    }
}
