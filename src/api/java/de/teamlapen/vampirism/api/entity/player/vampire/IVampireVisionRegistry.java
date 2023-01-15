package de.teamlapen.vampirism.api.entity.player.vampire;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 * Registry to register Vampire Player "visions"
 */
public interface IVampireVisionRegistry {

    /**
     * @return Return the id of the given vision, -1 if not registered
     * @deprecated use {@link #getVisionId(IVampireVision)}
     */
    @Deprecated(forRemoval = true)
    int getIdOfVision(IVampireVision vision);

    /**
     * @throws IllegalArgumentException if the vision is not registered
     */
    @NotNull
    ResourceLocation getVisionId(IVampireVision vision);

    /**
     * @return the vision belonging to the given id. Null if not found
     * @deprecated use {@link #getVision(net.minecraft.resources.ResourceLocation)}
     */
    @Nullable
    @Deprecated(forRemoval = true)
    IVampireVision getVisionOfId(int id);

    /**
     * @deprecated this is a helper method to migrate from the old id based system.
     */
    @Deprecated(forRemoval = true)
    ResourceLocation getIdForId(int id);

    /**
     * Retrieves the vision belonging to the given id
     */
    @Nullable
    IVampireVision getVision(ResourceLocation id);

    /**
     * @return An immutable copied list which contains all visions
     */
    @Unmodifiable
    List<IVampireVision> getVisions();

    /**
     *
     * @deprecated use {@link #registerVision(net.minecraft.resources.ResourceLocation, IVampireVision)}
     */
    @Deprecated(forRemoval = true)
    <T extends IVampireVision> T registerVision(String key, T vision);

    /**
     * Registers a new vision
     */
    <T extends IVampireVision> T registerVision(@NotNull ResourceLocation key, @NotNull T vision);
}
