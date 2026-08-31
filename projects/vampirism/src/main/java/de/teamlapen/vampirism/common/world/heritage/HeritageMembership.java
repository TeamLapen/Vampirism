package de.teamlapen.vampirism.common.world.heritage;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * The persisted heritage assignment of one player.
 */
public record HeritageMembership(UUID heritageId, HeritageOrigin origin, @Nullable UUID parentPlayerId, @Nullable String namedNpc, @Nullable Identifier definitionId) {
}
