package de.teamlapen.vampirism.api.world;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public interface ITotem {

    EntityType<? extends MobEntity> getCaptureEntityForFaction(IFaction<?> faction);

    @Nullable
    IFaction getCapturingFaction();

    @Nullable
    IFaction getControllingFaction();

    default World getTileWorld() {
        return ((TileEntity) this).getLevel();
    }

    @Nonnull
    AxisAlignedBB getVillageArea();

    @Nonnull
    AxisAlignedBB getVillageAreaReduced();

    default boolean isRaidTriggeredByBadOmen() { //TODO 1.17 remove default implementation
        return false;
    }

    default Optional<BlockPos> getVampireForestLocation() {
        return Optional.empty();
    }

}
