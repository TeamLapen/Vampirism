package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.entity.player.actions.IActionPlayer;
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * Interface for {@link IExtendedEntityProperties} which can unlock skills
 */
public interface ISkillPlayer extends IActionPlayer {
    /**
     * @return The skill handler for this player
     */
    ISkillHandler<? extends ISkillPlayer> getSkillHandler();
}
