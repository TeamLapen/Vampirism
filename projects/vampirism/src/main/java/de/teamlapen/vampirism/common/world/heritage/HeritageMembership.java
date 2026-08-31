package de.teamlapen.vampirism.common.world.heritage;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * The persisted heritage assignment of one player.
 */
public record HeritageMembership(UUID heritageId, HeritageOrigin origin, @Nullable UUID parentPlayerId, @Nullable String namedNpc, @Nullable String parentNpcId) {
}
