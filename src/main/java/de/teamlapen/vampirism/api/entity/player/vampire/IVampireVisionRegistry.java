package de.teamlapen.vampirism.api.entity.player.vampire;

import java.util.List;

/**
 * Registry to register Vampire Player "visions"
 */
public interface IVampireVisionRegistry {
    /**
     * @return A immutable copied list which contains all visions
     */
    List<IVampireVision> getVisions();

    <T extends IVampireVision> T registerVision(String key, T vision);
}
