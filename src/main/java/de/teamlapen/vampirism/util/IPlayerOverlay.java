package de.teamlapen.vampirism.util;

import com.mojang.authlib.GameProfile;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * Interface for creatures which have a biped model with a exchangeable face
 */
public interface IPlayerOverlay {

    /**
     * @return Game profile of the player who's face should be overlayed. Null if none
     */
    @OnlyIn(Dist.CLIENT)
    @Nullable
    GameProfile getOverlayPlayerProfile();
}
