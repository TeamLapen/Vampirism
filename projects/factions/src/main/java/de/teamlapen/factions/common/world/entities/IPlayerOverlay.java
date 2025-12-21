package de.teamlapen.factions.common.world.entities;

import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelType;
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
    Pair<Identifier, PlayerModelType> PENDING_PROP = Pair.of(DefaultPlayerSkin.getDefaultTexture(), PlayerModelType.WIDE);

    @NotNull
    Optional<PlayerSkinRenderCache.RenderInfo> getPlayerOverlay();
}
