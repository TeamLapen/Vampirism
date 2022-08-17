package de.teamlapen.vampirism.api.world;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
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
     * @param faction the faction for the capture entity
     * @return entityType of a capture entity
     */
    Optional<EntityType<? extends Mob>> getCaptureEntityForFaction(IFaction<?> faction);

    /**
     * @return current capturing faction of the totem or {@code null} if none
     */
    @Nullable
    IFaction<?> getCapturingFaction();

    /**
     * @return current controlling faction of the totem or {@code null} if none
     */
    @Nullable
    IFaction<?> getControllingFaction();

    /**
     * support method for getting block entity level
     *
     * @return level of the totem
     */
    default @Nullable Level getTileWorld() {
        return ((BlockEntity) this).getLevel();
    }

    /**
     * @return area of the village
     */
    @NotNull
    AABB getVillageArea();

    /**
     * @return reduced area of the village
     */
    @NotNull
    AABB getVillageAreaReduced();

    /**
     * @return whether a running raid was triggered by a faction BadOmen effect
     */
    boolean isRaidTriggeredByBadOmen();

}
