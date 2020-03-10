package de.teamlapen.vampirism.api.world;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IVillageAttributes {

    @Nullable
    IFaction getDefendingFaction();

    @Nullable
    IFaction getAttackingFaction();

    @Nonnull
    AxisAlignedBB getVillageArea();

    BlockPos getPosition();

    default boolean shouldForceTargets() {
        return false;
    }
}
