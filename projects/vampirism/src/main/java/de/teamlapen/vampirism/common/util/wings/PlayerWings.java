package de.teamlapen.vampirism.common.util.wings;

import de.teamlapen.vampirism.api.world.entity.player.vampire.IWingsEntity;

import java.util.Set;
import java.util.UUID;

public record PlayerWings(Set<IWingsEntity.Texture> textures) {
}
