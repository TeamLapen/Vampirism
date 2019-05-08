package de.teamlapen.vampirism.util;

import com.mojang.authlib.GameProfile;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Interface for creatures which have a biped model with a exchangeable face
 */
public interface IPlayerFace {


    /**
     * @return Game profile of the player who's face should be overlayed.
     */
    @SideOnly(Side.CLIENT)
    @Nullable
    GameProfile getPlayerFaceProfile();
}
