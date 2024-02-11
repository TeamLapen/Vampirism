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
     * @throws IllegalArgumentException if the vision is not registered
     */
    @NotNull
    ResourceLocation getVisionId(IVampireVision vision);

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
     * Registers a new vision
     */
    <T extends IVampireVision> T registerVision(@NotNull ResourceLocation key, @NotNull T vision);
}
