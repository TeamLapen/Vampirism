package de.teamlapen.vampirism.entity.minion;

import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;


public abstract class MinionEntity extends VampirismEntity {
    private final static Logger LOGGER = LogManager.getLogger();

    /**
     * Store the uuid of the lord. Should not be null when joining the world
     */
    @Nullable
    private UUID lordID;

    public MinionEntity(EntityType<? extends VampirismEntity> type, World world) {
        super(type, world);
    }

    @Nonnull
    public Optional<ILordPlayer> getLordOpt() {
        return Optional.ofNullable(getLord());
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (lordID == null) {
            LOGGER.warn("Minion without lord.");
            this.dead = true;
        }
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);
        this.lordID = nbt.hasUniqueId("lord") ? nbt.getUniqueId("lord") : null;
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        if (lordID != null) nbt.putUniqueId("lord", lordID);
    }

    @Nullable
    protected ILordPlayer getLord() {
        if (lordID == null) return null;
        PlayerEntity player = this.world.getPlayerByUuid(lordID);
        if (player == null || !player.isAlive()) return null;
        return FactionPlayerHandler.get(player);
    }
}
