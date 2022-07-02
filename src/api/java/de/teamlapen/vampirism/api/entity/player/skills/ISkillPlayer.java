package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.entity.player.actions.IActionPlayer;

import javax.annotation.Nonnull;

/**
 * Interface for a player's capability which can unlock skills
 */
public interface ISkillPlayer<T extends ISkillPlayer<?>> extends IActionPlayer<T> {
    /**
     * @return The skill handler for this player
     */
    @Nonnull
    ISkillHandler<T> getSkillHandler();
}
