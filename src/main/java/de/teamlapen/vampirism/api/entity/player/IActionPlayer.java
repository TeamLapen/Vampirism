package de.teamlapen.vampirism.api.entity.player;

import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * Interface for {@link IExtendedEntityProperties} that can use actions
 */
public interface IActionPlayer {
    IActionHandler<? extends IActionPlayer> getActionHandler();
}
