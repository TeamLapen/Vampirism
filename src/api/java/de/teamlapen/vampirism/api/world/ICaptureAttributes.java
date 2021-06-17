package de.teamlapen.vampirism.api.world;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public interface ICaptureAttributes {

    @Nullable
    IFaction getAttackingFaction();

    @Nullable
    IFaction getDefendingFaction();

    BlockPos getPosition();

    AxisAlignedBB getVillageArea();

    default boolean shouldForceTargets() {
        return false;
    }
}
