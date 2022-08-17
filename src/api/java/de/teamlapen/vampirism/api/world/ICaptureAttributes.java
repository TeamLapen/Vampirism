package de.teamlapen.vampirism.api.world;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

/**
 * used for {@link de.teamlapen.vampirism.api.entity.IVillageCaptureEntity#attackVillage(ICaptureAttributes)} and {@link de.teamlapen.vampirism.api.entity.IVillageCaptureEntity#defendVillage(ICaptureAttributes)}
 */
public interface ICaptureAttributes {

    /**
     * @return currently attacking faction of the village
     */
    @Nullable
    IFaction<?> getAttackingFaction();

    /**
     * @return currently defending faction of the village
     */
    @Nullable
    IFaction<?> getDefendingFaction();

    /**
     * @return totem position
     */
    BlockPos getPosition();

    /**
     * @return village area
     */
    AABB getVillageArea();

    /**
     * @return whether AI goals should target opposing factions without see restrictions
     */
    default boolean shouldForceTargets() {
        return false;
    }
}
