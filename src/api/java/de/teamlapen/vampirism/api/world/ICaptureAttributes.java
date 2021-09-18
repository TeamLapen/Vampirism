package de.teamlapen.vampirism.api.world;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

public interface ICaptureAttributes {

    @Nullable
    IFaction getAttackingFaction();

    @Nullable
    IFaction getDefendingFaction();

    BlockPos getPosition();

    AABB getVillageArea();

    default boolean shouldForceTargets() {
        return false;
    }
}
