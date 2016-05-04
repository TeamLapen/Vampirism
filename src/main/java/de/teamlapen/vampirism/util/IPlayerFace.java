package de.teamlapen.vampirism.util;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Interface for creatures which have a biped model with a exchangeable face
 */
public interface IPlayerFace {

    /**
     * @return Name of the player who's face should be overlayed. Null if none
     */
    @SideOnly(Side.CLIENT)
    @Nullable
    String getPlayerFaceName();
}
