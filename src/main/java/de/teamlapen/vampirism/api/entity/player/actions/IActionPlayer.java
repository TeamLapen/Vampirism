package de.teamlapen.vampirism.api.entity.player.actions;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * Interface for {@link IExtendedEntityProperties} that can use actions
 */
public interface IActionPlayer<T extends IActionPlayer> {
    IActionHandler<T> getActionHandler();

    EntityPlayer getRepresentingPlayer();
}
