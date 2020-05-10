package de.teamlapen.vampirism.api.entity.player.skills;


/**
 * Handles the players skills
 */
public interface ISkillHandler<T extends ISkillPlayer<?>> {

    /**
     * @param skill
     * @return Returns false if the skill already is unlocked or the parent node is not unlocked or the skill is not found
     */
    Result canSkillBeEnabled(ISkill skill);


    /**
     * Disables the given skill
     *
     * @param skill
     */
    void disableSkill(ISkill skill);

    /**
     * Enable the given skill. Check canSkillBeEnabled first
     *
     * @param skill
     */
    void enableSkill(ISkill skill);

    /**
     * @return The count of additional skills that can be currently unlocked
     */
    int getLeftSkillPoints();

    ISkill[] getParentSkills(ISkill skill);

    boolean isSkillEnabled(ISkill skill);

    /**
     * Reset all skills but reactivate the root skill of the faction
     */
    void resetSkills();

    enum Result {
        OK, ALREADY_ENABLED, PARENT_NOT_ENABLED, NOT_FOUND, NO_POINTS, OTHER_NODE_SKILL
    }
}
