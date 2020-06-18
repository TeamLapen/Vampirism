package de.teamlapen.vampirism.api.world;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ITotem {

    @Nullable
    IFaction getCapturingFaction();

    @Nullable
    IFaction getControllingFaction();

    @Nonnull
    AxisAlignedBB getVillageArea();

    @Nonnull
    AxisAlignedBB getVillageAreaReduced();

    void updateTrainer(boolean toDummy);

    EntityType<? extends MobEntity> getCaptureEntityForFaction(IFaction<?> faction);
}
