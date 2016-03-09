package de.teamlapen.vampirism.api.entity.player.skills;

/**
 * Handles the players skills
 */
public interface ISkillHandler<T extends ISkillPlayer> {

    /**
     * @param skill
     * @return Returns false if the skill already is unlocked or the parent node is not unlocked or the skill is not found
     */
    boolean canSkillBeEnabled(ISkill skill);


    /**
     * Disables the given skill
     *
     * @param skill
     */
    void disableSkill(ISkill skill);

    /**
     * Enable the given skill. Check canSkillBeEnabled first
     * @param skill
     */
    void enableSkill(ISkill skill);

    /**
     * @return The count of additional skills that can be currently unlocked
     */
    int getLeftSkillPoints();

    boolean isNodeEnabled(SkillNode node);

    boolean isSkillEnabled(ISkill skill);
}
