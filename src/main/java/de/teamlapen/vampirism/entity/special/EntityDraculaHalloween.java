package de.teamlapen.vampirism.entity.special;

import com.google.common.base.Optional;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.EntityAreaParticleCloud;
import de.teamlapen.vampirism.entity.EntityVampirism;
import de.teamlapen.vampirism.util.HalloweenSpecial;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityDraculaHalloween extends EntityVampirism {

    protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.createKey(EntityTameable.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    private int seen = 0;
    private int hiding = 0;

    public EntityDraculaHalloween(World world) {
        super(world);
        this.setEntityInvulnerable(true);
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }


    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(1, new EntityAIWatchClosest(this, EntityPlayer.class, 10, 1));
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!HalloweenSpecial.isEnabled()) {
            this.setDead();
        }
        if (this.world.isRemote) {
            EntityLivingBase owner = getOwner();
            if (owner != null && !isInvisible() && !VampirismMod.proxy.isPlayerThePlayer((EntityPlayer) owner)) {
                this.setInvisible(true);
                VampirismMod.log.t("Setting invisible on other client");
            }
            return;
        }
        EntityLivingBase owner = getOwner();
        if (owner == null) {
            this.setDead();
            return;
        }
        if (hiding == 0) {
            if (UtilLib.canReallySee(owner, this, true)) {
                seen++;
                if (seen == 1) {
                    if (owner instanceof EntityPlayerMP) {
                        ((EntityPlayerMP) owner).connection.sendPacket(new SPacketSoundEffect(ModSounds.entity_vampire_scream, SoundCategory.NEUTRAL, posX, posY, posZ, 1, 1));
                    }
                }
            } else if (this.getDistanceSq(owner) > 5) {
                teleportBehind(owner);
            }


            if (seen > 5) {

                EntityAreaParticleCloud particleCloud = new EntityAreaParticleCloud(getEntityWorld());
                particleCloud.setPosition(posX, posY, posZ);
                particleCloud.setRadius(0.7F);
                particleCloud.setHeight(this.height);
                particleCloud.setDuration(3);
                particleCloud.setSpawnRate(10);
                getEntityWorld().spawnEntity(particleCloud);

                if (this.getRNG().nextInt(3) == 0) {
                    teleportBehind(owner);
                } else {
                    this.setInvisible(true);
                    BlockPos spawn = world.getSpawnPoint();
                    this.setPosition(spawn.getX(), 3, spawn.getZ());
                    hiding = this.getRNG().nextInt(6000);
                }
                this.seen = 0;
            }
        } else {
            hiding--;
            if (hiding == 0) {
                teleportBehind(owner);
                this.setInvisible(true);

            }

        }


    }

    private void teleportBehind(EntityLivingBase target) {
        BlockPos behind = UtilLib.getPositionBehindEntity(target, 1.5F);
        this.setPosition(behind.getX(), target.posY, behind.getZ());

        if (!this.isNotColliding()) {
            int y = getEntityWorld().getHeight(behind).getY();
            this.setPosition(behind.getX(), y, behind.getZ());
        }
    }

    @Nullable
    public UUID getOwnerId() {
        return (UUID) ((Optional) this.dataManager.get(OWNER_UNIQUE_ID)).orNull();
    }

    public void setOwnerId(@Nullable UUID p_184754_1_) {
        this.dataManager.set(OWNER_UNIQUE_ID, Optional.fromNullable(p_184754_1_));
    }


    @Nullable
    public EntityLivingBase getOwner() {
        try {
            UUID uuid = this.getOwnerId();
            return uuid == null ? null : this.world.getPlayerEntityByUUID(uuid);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(OWNER_UNIQUE_ID, Optional.absent());
    }


    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return super.writeToNBT(compound);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setDead();
    }
}
