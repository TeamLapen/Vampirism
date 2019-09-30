package de.teamlapen.vampirism.api.world;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public interface IVillageAttributes {

    @Nullable
    IPlayableFaction getDefendingFaction();

    @Nullable
    IPlayableFaction getAttackingFaction();

    AxisAlignedBB getVillageArea();

    BlockPos getPosition();

    default boolean shouldForceTargets() {
        return false;
    }
}
