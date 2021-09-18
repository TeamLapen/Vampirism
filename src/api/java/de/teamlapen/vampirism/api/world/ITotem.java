package de.teamlapen.vampirism.api.world;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public interface ITotem {

    Optional<EntityType<? extends Mob>> getCaptureEntityForFaction(IFaction<?> faction);

    @Nullable
    IFaction getCapturingFaction();

    @Nullable
    IFaction getControllingFaction();

    default Level getTileWorld() {
        return ((BlockEntity) this).getLevel();
    }

    @Nonnull
    AABB getVillageArea();

    @Nonnull
    AABB getVillageAreaReduced();

    boolean isRaidTriggeredByBadOmen();

}
