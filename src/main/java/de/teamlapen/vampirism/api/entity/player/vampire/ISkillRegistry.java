package de.teamlapen.vampirism.api.entity.player.vampire;

import java.util.List;

/**
 * Registry for vampire skills.
 * Never use the Integer id's here, they are only intended to be used for sync and to update timers
 */
public interface ISkillRegistry {
    /**
     * @param player
     * @return A list of all skills the player can currently use
     */
    List<IVampireSkill> getAvailableSkills(IVampirePlayer player);

    /**
     * @param skill
     * @return the key which maps to the given skill
     */
    String getKeyFromSkill(IVampireSkill skill);

    int getSkillCount();

    /**
     * @param key
     * @return the skill that is registered with the given key
     */
    IVampireSkill getSkillFromKey(String key);

    /**
     * Register a skill
     * Preferably during init
     *
     * @param skill
     * @return The same skill
     */
    <T extends IVampireSkill> T registerSkill(T skill, String key);
}
