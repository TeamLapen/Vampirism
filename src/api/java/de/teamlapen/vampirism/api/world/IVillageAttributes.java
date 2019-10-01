package de.teamlapen.vampirism.api.world;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public interface IVillageAttributes {

    @Nullable
    IFaction getDefendingFaction();

    @Nullable
    IFaction getAttackingFaction();

    AxisAlignedBB getVillageArea();

    BlockPos getPosition();

    default boolean shouldForceTargets() {
        return false;
    }
}
