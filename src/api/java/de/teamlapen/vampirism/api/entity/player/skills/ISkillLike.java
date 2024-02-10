package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;

/**
 * Interface representing objects that are effectively a skill. This is extended by {@link ISkill} and {@link de.teamlapen.vampirism.api.entity.player.actions.IAction}
 */
public interface ISkillLike<T extends IFactionPlayer<T>> {

    /**
     * Get the object as skill
     */
    ISkill<T> asSkill();
}
