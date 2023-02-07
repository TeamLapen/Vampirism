package de.teamlapen.vampirism.util;

import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Interface for creatures which have a biped model with parts of their bodies overlayed with the texture of a player
 */
public interface IPlayerOverlay {
    /**
     * Used as overlay description while waiting for the texture to be loaded
     */
    Pair<ResourceLocation, Boolean> PENDING_PROP = Pair.of(DefaultPlayerSkin.getDefaultSkin(), false);

    /**
     * @return Description of the overlay. Textures loc and boolean (true: slimArms, false: normal)
     */
    @OnlyIn(Dist.CLIENT)
    @NotNull
    Optional<Pair<ResourceLocation, Boolean>> getOverlayPlayerProperties();
}
