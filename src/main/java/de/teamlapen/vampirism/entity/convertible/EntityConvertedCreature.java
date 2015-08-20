package de.teamlapen.vampirism.entity.convertible;

import de.teamlapen.vampirism.entity.EntityHunterBase;
import de.teamlapen.vampirism.entity.EntityVampireBase;
import de.teamlapen.vampirism.entity.ai.VampireAIFleeSun;
import de.teamlapen.vampirism.network.ISyncable;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * Converted creature class.
 * Contains (stores and syncs) a normal Entity for rendering purpose
 */
public class EntityConvertedCreature extends EntityVampireBase implements ISyncable {

    private EntityCreature entityCreature;
    private boolean entityChanged = false;

    public EntityConvertedCreature(World world) {
        super(world);

        this.tasks.addTask(1, new EntityAIAvoidEntity(this, EntityHunterBase.class, BALANCE.MOBPROP.VAMPIRE_DISTANCE_HUNTER, 1.0, 1.05));

        this.tasks.addTask(3, new VampireAIFleeSun(this, 1F));
        this.tasks.addTask(4, new EntityAIRestrictSun(this));
        tasks.addTask(5, new net.minecraft.entity.ai.EntityAIAttackOnCollide(this, EntityPlayer.class, 0.9D, false));
        tasks.addTask(5, new net.minecraft.entity.ai.EntityAIAttackOnCollide(this, EntityHunterBase.class, 1.0D, true));


        this.tasks.addTask(11, new EntityAIWander(this, 0.7));
        this.tasks.addTask(13, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(15, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
    }


    public void setEntityCreature(EntityCreature creature) {
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
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        if (!nil()) {
            NBTTagCompound entity = new NBTTagCompound();
            entityCreature.isDead = false;
            entityCreature.writeToNBTOptional(entity);
            entityCreature.isDead = true;
            nbt.setTag("entity_old", entity);
        }

    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.updateEntityAttributes();
    }


    @Override
    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
        getConvertingHandler().dropConvertedItems(entityCreature, p_70628_1_, p_70628_2_);
    }

    protected void updateEntityAttributes() {
        if (!nil()) {
            ConvertingHandler handler = getConvertingHandler();
            this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(handler.getConvertedDMG(entityCreature));
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(handler.getConvertedMaxHealth(entityCreature));
            this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(handler.getConvertedKnockBackResistance(entityCreature));
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(handler.getConvertedSpeed(entityCreature));
        } else {
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(1000);
            this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(0);
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0);
        }

    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("entity_old")) {
            setEntityCreature((EntityCreature) EntityList.createEntityFromNBT(nbt.getCompoundTag("entity_old"), worldObj));
        } else {
            Logger.t("Converted does not have old");
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!worldObj.isRemote && entityCreature == null) {
            Logger.t("Setting dead");
            this.setDead();
        }
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
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

            if (worldObj.isRemote) {
                entityCreature.serverPosX = this.serverPosX;
                entityCreature.serverPosY = this.serverPosY;
                entityCreature.serverPosZ = this.serverPosZ;

            }
        }
        if (entityChanged) {
            this.updateEntityAttributes();
        }
    }

    @Override
    public void loadUpdateFromNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("entity")) {
            setEntityCreature((EntityCreature) EntityList.createEntityFromNBT(nbt.getCompoundTag("entity"), worldObj));
        }
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public void writeFullUpdateToNBT(NBTTagCompound nbt) {
        if (entityCreature != null) {
            NBTTagCompound entity = new NBTTagCompound();
            entityCreature.isDead = false;
            entityCreature.writeToNBTOptional(entity);
            entityCreature.isDead = true;
            nbt.setTag("entity", entity);
        }

    }

    public EntityCreature getEntityCreature() {
        return this.entityCreature;
    }

    @Override
    public String getCommandSenderName() {
        return StatCollector.translateToLocal("entity.vampirism.vampire.name") + " " + (nil() ? super.getCommandSenderName() : entityCreature.getCommandSenderName());
    }

    @Override
    public String toString() {
        return "[" + super.toString() + " representing " + entityCreature + "]";
    }

    @Override
    public void playLivingSound() {
        if (!nil()) {
            entityCreature.playLivingSound();
        }
    }

    protected ConvertingHandler getConvertingHandler() {
        return BiteableRegistry.getEntry(entityCreature).convertingHandler;
    }
    protected boolean nil() {
        return entityCreature == null;
    }
}
