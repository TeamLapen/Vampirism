package de.teamlapen.vampirism.entity.minion;

import com.mojang.authlib.GameProfile;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.util.IPlayerOverlay;
import de.teamlapen.vampirism.util.PlayerSkinHelper;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;


public abstract class MinionEntity extends VampirismEntity implements IPlayerOverlay {
    private final static Logger LOGGER = LogManager.getLogger();

    /**
     * Store the uuid of the lord. Should not be null when joining the world
     */
    protected static final DataParameter<Optional<UUID>> LORD_ID = EntityDataManager.createKey(TameableEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);


    @Nullable
    private GameProfile skinProfile;

    @Nullable
    @Override
    public GameProfile getOverlayPlayerProfile() {
        if (skinProfile == null) {
            skinProfile = this.getLordID().map(id->new GameProfile(id,"")).orElse(null);
            if(skinProfile!=null)PlayerSkinHelper.updateGameProfileAsync(skinProfile, updatedProfile->this.skinProfile=updatedProfile);
        }
        return skinProfile;
    }

    public MinionEntity(EntityType<? extends VampirismEntity> type, World world) {
        super(type, world);
    }

    @Nonnull
    public Optional<ILordPlayer> getLordOpt() {
        return Optional.ofNullable(getLord());
    }

    public float getScale() {
        return 0.8f;
    }

    @Override
    public EntitySize getSize(Pose p_213305_1_) {
        return super.getSize(p_213305_1_).scale(getScale());
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (!this.world.isRemote && !this.getLordID().isPresent()) {
            LOGGER.warn("Minion without lord.");
            this.dead = true;
        }
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);
        UUID id = nbt.hasUniqueId("lord") ? nbt.getUniqueId("lord") : null;
        this.getDataManager().set(LORD_ID, Optional.ofNullable(id));
    }

    /**
     * DON't use
     * Called to remove entity from world on call from lord.
     * Does checkin minion
     */
    @Deprecated
    public void recallMinion() {
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        this.getLordID().ifPresent(id -> nbt.putUniqueId("lord", id));
    }

    @Nullable
    protected ILordPlayer getLord() {
        return this.getLordID().map(this.world::getPlayerByUuid).filter(PlayerEntity::isAlive).map(FactionPlayerHandler::get).orElse(null);
    }

    protected Optional<UUID> getLordID() {
        return this.getDataManager().get(LORD_ID);
    }

    public void setLordID(@Nonnull UUID lord) {
        this.getDataManager().set(LORD_ID, Optional.of(lord));
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        getAttributes().registerAttribute(VReference.sunDamage).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_MOB_SUN_DAMAGE);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.getDataManager().register(LORD_ID, Optional.empty());
    }
}
