package de.teamlapen.vampirism.api.world;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ITotem {

    EntityType<? extends MobEntity> getCaptureEntityForFaction(IFaction<?> faction);

    @Nullable
    IFaction getCapturingFaction();

    @Nullable
    IFaction getControllingFaction();

    @Nonnull
    AxisAlignedBB getVillageArea();

    @Nonnull
    AxisAlignedBB getVillageAreaReduced();

    default World getTileWorld() {
        return ((TileEntity) this).getWorld();
    }

}
