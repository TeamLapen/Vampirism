package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.entity.ai.EntityAIAttackMeleeNoSun;
import de.teamlapen.vampirism.entity.vampire.EntityVampireBase;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Converted creature class.
 * Contains (stores and syncs) a normal Entity for rendering purpose
 */
public class EntityConvertedCreature<T extends EntityCreature> extends EntityVampireBase implements IConvertedCreature<T>, ISyncable {
    private final static Logger LOGGER = LogManager.getLogger(EntityConvertedCreature.class);
    private T entityCreature;
    private boolean entityChanged = false;
    private boolean canDespawn = false;

    public EntityConvertedCreature(World world) {
        super(world, false);

    }

    @Override
    public ITextComponent getName() {
        return UtilLib.translate("entity.vampirism.vampire.name") + " " + (nil() ? super.getName() : entityCreature.getName());
    }

    public T getOldCreature() {
        return entityCreature;
    }

    @Override
    public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount) {
        if (forSpawnCount && type == EnumCreatureType.CREATURE) return true;
        return super.isCreatureType(type, forSpawnCount);
    }

    @Override
    public void loadUpdateFromNBT(NBTTagCompound nbt) {
        if (nbt.contains("entity_old")) {
            setEntityCreature((T) EntityType.create(nbt.getCompound("entity_old"), getEntityWorld()));
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (!nil()) {
            entityCreature.copyLocationAndAnglesFrom(this);
            entityCreature.prevPosZ = this.prevPosZ;
            entityCreature.prevPosY = this.prevPosY;
            entityCreature.prevPosX = this.prevPosX;
            entityCreature.rotationYawHead = this.rotationYawHead;
            entityCreature.prevRotationPitch = this.prevRotationPitch;
            entityCreature.prevRotationYaw = this.prevRotationYaw;
            entityCreature.prevRotationYawHead = this.prevRotationYawHead;
            entityCreature.motionX = this.motionX;
            entityCreature.motionY = this.motionY;
            entityCreature.motionZ = this.motionZ;
            entityCreature.lastTickPosX = this.lastTickPosX;
            entityCreature.lastTickPosY = this.lastTickPosY;
            entityCreature.lastTickPosZ = this.lastTickPosZ;
            entityCreature.hurtTime = this.hurtTime;
            entityCreature.maxHurtTime = this.maxHurtTime;
            entityCreature.attackedAtYaw = this.attackedAtYaw;
            entityCreature.swingProgress = this.swingProgress;
            entityCreature.prevSwingProgress = this.prevSwingProgress;
            entityCreature.prevLimbSwingAmount = this.prevLimbSwingAmount;
            entityCreature.limbSwingAmount = this.limbSwingAmount;
            entityCreature.limbSwing = this.limbSwing;
            entityCreature.renderYawOffset = this.renderYawOffset;
            entityCreature.prevRenderYawOffset = this.prevRenderYawOffset;
            entityCreature.deathTime = this.deathTime;

            if (world.isRemote) {
                entityCreature.serverPosX = this.serverPosX;
                entityCreature.serverPosY = this.serverPosY;
                entityCreature.serverPosZ = this.serverPosZ;

            }
        }
        if (entityChanged) {
            this.updateEntityAttributes();
            entityChanged = false;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!world.isRemote && entityCreature == null) {
            LOGGER.debug("Setting dead, since creature is null");
            this.remove();
        }
    }

    @Override
    public void playLivingSound() {
        if (!nil()) {
            entityCreature.playLivingSound();
        }
    }

    @Override
    public void readAdditional(NBTTagCompound nbt) {
        super.readAdditional(nbt);
        if (nbt.contains("entity_old")) {
            setEntityCreature((T) EntityType.create(nbt.getCompound("entity_old"), world));
            if (nil()) {
                LOGGER.warn("Failed to create old entity %s. Maybe the entity does not exist anymore", nbt.getCompound("entity_old"));
            }
        } else {
            LOGGER.warn("Saved entity did not have a old entity");
        }
        if (nbt.contains("converted_canDespawn")) {
            canDespawn = nbt.getBoolean("converted_canDespawn");
        }
    }

    /**
     * Allows the entity to despawn
     */
    public void setCanDespawn() {
        canDespawn = true;
    }

    /**
     * Set the old creature (the one before conversion)
     *
     * @param creature
     */
    public void setEntityCreature(T creature) {
        if ((creature == null && entityCreature != null)) {
            entityChanged = true;
            entityCreature = null;
        } else if (creature != null) {
            if (!creature.equals(entityCreature)) {
                entityCreature = creature;
                entityChanged = true;
                this.setSize(creature.width, creature.height);
            }
        }
        if (entityCreature != null && getConvertedHelper() == null) {
            entityCreature = null;
            LOGGER.warn("Cannot find converting handler for converted creature %s (%s)", this, entityCreature);
        }
    }

    @Override
    public String toString() {
        return "[" + super.toString() + " representing " + entityCreature + "]";
    }

    @Override
    public void writeAdditional(NBTTagCompound nbt) {
        super.writeAdditional(nbt);
        writeOldEntityToNBT(nbt);
        nbt.putBoolean("converter_canDespawn", canDespawn);

    }

    @Override
    public void writeFullUpdateToNBT(NBTTagCompound nbt) {
        writeOldEntityToNBT(nbt);

    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.updateEntityAttributes();
    }

    @Override
    public boolean canDespawn() {
        return canDespawn;
    }

    @Override
    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
        getConvertedHelper().dropConvertedItems(this, entityCreature, p_70628_1_, p_70628_2_);
    }

    /**
     * @return The {@link de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler.IDefaultHelper} for this creature
     */
    protected IConvertingHandler.IDefaultHelper getConvertedHelper() {
        IConvertingHandler handler = VampirismAPI.entityRegistry().getEntry(entityCreature).convertingHandler;
        if (handler instanceof DefaultConvertingHandler) {
            return ((DefaultConvertingHandler) handler).getHelper();
        }
        return null;
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(1, new EntityAIAvoidEntity(this, EntityCreature.class, 10, 1.0, 1.1, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, VReference.HUNTER_FACTION)));
        //this.tasks.addTask(3, new VampireAIFleeSun(this, 1F));
        this.tasks.addTask(4, new EntityAIRestrictSun(this));
        tasks.addTask(5, new EntityAIAttackMeleeNoSun(this, 0.9D, false));
        this.experienceValue = 2;

        this.tasks.addTask(11, new EntityAIWander(this, 0.7));
        this.tasks.addTask(13, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(15, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, true, false, null)));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityCreature.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)));
    }

    protected boolean nil() {
        return entityCreature == null;
    }

    protected void updateEntityAttributes() {
        if (!nil()) {
            IConvertingHandler.IDefaultHelper helper = getConvertedHelper();
            this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(helper.getConvertedDMG(entityCreature));
            this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(helper.getConvertedMaxHealth(entityCreature));
            this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(helper.getConvertedKnockbackResistance(entityCreature));
            this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(helper.getConvertedSpeed(entityCreature));
        } else {
            this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1000);
            this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0);
            this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0);
        }

    }

    /**
     * Write the old entity to nbt
     *
     * @param nbt
     */
    private void writeOldEntityToNBT(NBTTagCompound nbt) {
        if (!nil()) {
            try {
                NBTTagCompound entity = new NBTTagCompound();
                entityCreature.isDead = false;
                entityCreature.writeUnlessPassenger(entity);
                entityCreature.isDead = true;
                nbt.put("entity_old", entity);
            } catch (Exception e) {
                LOGGER.error(e, "Failed to write old entity (%s) to NBT. If this happens more often please report this to the mod author.", entityCreature);
                this.setEntityCreature(null);
            }
        }

    }
}
