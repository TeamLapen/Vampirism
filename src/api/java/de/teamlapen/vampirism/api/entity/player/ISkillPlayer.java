package de.teamlapen.vampirism.api.entity.player;

import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import org.jetbrains.annotations.NotNull;

public interface ISkillPlayer<T extends ISkillPlayer<T>> extends IFactionPlayer<T> {

    /**
     * @return The skill handler for this player
     */
    @NotNull
    ISkillHandler<T> getSkillHandler();

    IActionHandler<T> getActionHandler();
}
