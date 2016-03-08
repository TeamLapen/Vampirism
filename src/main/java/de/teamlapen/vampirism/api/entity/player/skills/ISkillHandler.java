package de.teamlapen.vampirism.api.entity.player.skills;

/**
 * Created by Max on 08.03.2016.
 */
public interface ISkillHandler<T extends ISkillPlayer> {

    /**
     * @param skill
     * @return Returns false if the skill already is unlocked or the parent node is not unlocked or the skill is not found
     */
    boolean canSkillBeEnabled(ISkill skill);

    void disableSkill(ISkill skill);

    void enableSkill(ISkill skill);

    boolean isNodeEnabled(SkillNode skill);

    boolean isSkillEnabled(ISkill skill);
}
