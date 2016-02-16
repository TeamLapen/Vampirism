package de.teamlapen.vampirism.api.entity.player.vampire;

import java.util.List;

/**
 * Interface for {@link IVampirePlayer}'s skill handler
 */
public interface ISkillHandler {


    /**
     * Returns +Ticks_Left/Total_Duration(Positive) if skill is active
     * Returns -Cooldown_Left/Total_Cooldown(Negative) if skill is in cooldown
     *
     * @param skill
     * @return
     */
    float getPercentageForSkill(IVampireSkill skill);

    List<IVampireSkill> getAvailableSkills();

    /**
     * toggle the skill (server side)
     *
     * @param skill
     * @return
     */
    IVampireSkill.PERM toggleSkill(IVampireSkill skill);

    /**
     * Checks if the skill is currently activated
     *
     * @param skill
     * @return
     */
    boolean isSkillActive(ILastingVampireSkill skill);

    /**
     * Checks if the lasting skill is currently activated.
     * Prefer {@link ISkillHandler#isSkillActive(ILastingVampireSkill)} over this one
     *
     * @return
     */
    boolean isSkillActive(String id);

    /**
     * Set all timers to 0
     */
    void resetTimers();

    void deactivateAllSkills();
}
