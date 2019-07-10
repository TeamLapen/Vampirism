package de.teamlapen.vampirism.entity.special;


import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.AreaParticleCloudEntity;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.util.HalloweenSpecial;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;


/**
 * Quick and dirty
 * Only used on halloween
 */
public class DraculaHalloweenEntity extends VampirismEntity {

    private final static Logger LOGGER = LogManager.getLogger(DraculaHalloweenEntity.class);
    protected static final DataParameter<java.util.Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.createKey(TameableEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    private int seen = 0;
    private int hiding = 0;
    private boolean particle = false;

    public DraculaHalloweenEntity(EntityType<? extends DraculaHalloweenEntity> type, World world) {
        super(type, world);
        this.setInvulnerable(true);
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Nullable
    public LivingEntity getOwner() {
        try {
            UUID uuid = this.getOwnerId();
            return uuid == null ? null : this.world.getPlayerByUuid(uuid);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

    @Nullable
    public UUID getOwnerId() {
        return this.dataManager.get(OWNER_UNIQUE_ID).orElse(null);
    }

    public void setOwnerId(@Nullable UUID p_184754_1_) {
        this.dataManager.set(OWNER_UNIQUE_ID, Optional.ofNullable(p_184754_1_));
    }

    public boolean isParticle() {
        return particle;
    }

    public void setParticle(boolean particle) {
        this.particle = particle;
    }

    public void makeHide(int time) {
        seen = 0;
        this.setInvisible(true);
        BlockPos spawn = world.getSpawnPoint();
        this.setPosition(spawn.getX(), 3, spawn.getZ());
        hiding = time;
    }

    @Override
    public void livingTick() {
        super.livingTick();

        if (!HalloweenSpecial.isEnabled()) {
            this.remove();
        }
        if (this.world.isRemote) {
            LivingEntity owner = getOwner();
            if (owner != null && !isInvisible() && !VampirismMod.proxy.isPlayerThePlayer((PlayerEntity) owner)) {
                this.setInvisible(true);
                LOGGER.info("Setting invisible on other client");
            }
            return;
        }
        LivingEntity owner = getOwner();
        if (owner == null) {
            this.remove();
            return;
        }
        if (this.getEntityWorld().isDaytime()) {
            if (hiding == 0) {
                makeHide(this.getRNG().nextInt(1000));
            }
            return;
        }
        if (hiding == 0) {
            if (UtilLib.canReallySee(owner, this, true)) {
                seen++;
                if (seen == 1) {
                    if (owner instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity) owner).connection.sendPacket(new SPlaySoundEffectPacket(ModSounds.entity_vampire_scream, SoundCategory.NEUTRAL, posX, posY, posZ, 2, 1));
                    }
                }
            } else if (this.getDistanceSq(owner) > 5) {
                teleportBehind(owner);
            }


            if (seen > 5) {

                AreaParticleCloudEntity particleCloud = ModEntities.particle_cloud.create(this.getEntityWorld());
                particleCloud.setPosition(posX, posY, posZ);
                particleCloud.setRadius(0.7F);
                particleCloud.setHeight(this.getHeight());
                particleCloud.setDuration(3);
                particleCloud.setSpawnRate(10);
                getEntityWorld().addEntity(particleCloud);

                if (this.getRNG().nextInt(3) == 0) {
                    teleportBehind(owner);
                } else {
                    if (this.getRNG().nextInt(3) == 0) {
                        this.remove();
                    } else {
                        makeHide(this.getRNG().nextInt(3000));
                    }
                }
                this.seen = 0;
            }
        } else {
            hiding--;
            if (hiding == 0) {
                teleportBehind(owner);
                this.setInvisible(false);
            }

        }


    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.remove();
    }

    @Override
    public CompoundNBT writeWithoutTypeId(CompoundNBT compound) {
        return super.writeWithoutTypeId(compound);
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(OWNER_UNIQUE_ID, Optional.empty());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new LookAtGoal(this, PlayerEntity.class, 10, 1));
    }

    private void teleportBehind(LivingEntity target) {
        BlockPos behind = UtilLib.getPositionBehindEntity(target, 1.5F);
        this.setPosition(behind.getX(), target.posY, behind.getZ());

        if (!this.isNotColliding(getEntityWorld())) {
            int y = getEntityWorld().getHeight(Heightmap.Type.WORLD_SURFACE, behind).getY();
            this.setPosition(behind.getX(), y, behind.getZ());
        }
    }
}
