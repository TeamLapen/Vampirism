package de.teamlapen.vampirism.api.entity.player.vampire;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Registry to register Vampire Player "visions"
 */
public interface IVampireVisionRegistry {
    /**
     * @return Return the id of the given vision, -1 if not registered
     */
    int getIdOfVision(IVampireVision vision);

    /**
     * @return the vision belonging to the given id. Null if not found
     */
    @Nullable
    IVampireVision getVisionOfId(int id);

    /**
     * @return An immutable copied list which contains all visions
     */
    List<IVampireVision> getVisions();

    <T extends IVampireVision> T registerVision(String key, T vision);
}
