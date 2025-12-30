package de.teamlapen.faction.api.world;

import de.teamlapen.faction.api.factions.IFaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Implemented in TotemBlockEntity
 */
public interface ITotem {

    /**
     * @return current capturing faction of the totem or {@code null} if none
     */
    @Nullable
    Holder<? extends IFaction<?>> getCapturingFaction();

    /**
     * @return current controlling faction of the totem or {@code null} if none
     */
    @NotNull
    Holder<? extends IFaction<?>> getControllingFaction();

    /**
     * support method for getting block entity level
     *
     * @return level of the totem
     */
    default @Nullable Level getTileLevel() {
        return ((BlockEntity) this).getLevel();
    }

    /**
     * @return area of the village
     */
    AABB getVillageArea();

    /**
     * @return reduced area of the village
     */
    AABB getVillageAreaReduced();

    /**
     * @return whether a running raid was triggered by a faction BadOmen effect
     */
    boolean isRaidTriggeredByBadOmen();

    default Optional<BlockPos> getVampireForestLocation() {
        return Optional.empty();
    }

    BlockPos position();

}
