package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.entity.ai.VampireAIFleeSun;
import de.teamlapen.vampirism.network.ISyncable;
import de.teamlapen.vampirism.util.BALANCE;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
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

        this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityHunterBase.class, BALANCE.MOBPROP.VAMPIRE_DISTANCE_HUNTER, 1.2, 1.4));
        this.tasks.addTask(3, new EntityAIRestrictSun(this));
        this.tasks.addTask(4, new VampireAIFleeSun(this, 1.2F));
        tasks.addTask(5, new net.minecraft.entity.ai.EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
        tasks.addTask(5, new net.minecraft.entity.ai.EntityAIAttackOnCollide(this, EntityHunterBase.class, 1.0D, true));

        this.tasks.addTask(10, new EntityAIMoveThroughVillage(this, 0.6, false));
        this.tasks.addTask(11, new EntityAIWander(this, 0.7));
        this.tasks.addTask(12, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(13, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
    }

    public static EntityConvertedCreature createFrom(EntityCreature creature) {
        EntityConvertedCreature convertedCreature = new EntityConvertedCreature(creature.worldObj);
        convertedCreature.copyLocationAndAnglesFrom(creature);
        convertedCreature.setEntityCreature(creature);
        convertedCreature.setSize(creature.width, creature.height);
        convertedCreature.addPotionEffect(new PotionEffect(Potion.weakness.id, 200, 2));
        convertedCreature.addPotionEffect(new PotionEffect(Potion.regeneration.id, 200, 1));
        return convertedCreature;
    }

    protected void setEntityCreature(EntityCreature creature) {
        if (creature == null && entityCreature != null) {
            entityChanged = true;
            entityCreature = null;
        } else {
            if (!creature.equals(entityCreature)) {
                entityCreature = creature;
                entityChanged = true;
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
            nbt.setTag("entity", entity);
        }

    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.updateEntityAttributes();
    }

    protected void updateEntityAttributes() {
        if (!nil()) {
            IAttributeInstance dmg = entityCreature.getEntityAttribute(SharedMonsterAttributes.attackDamage);
            if (dmg != null) {
                this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(dmg.getBaseValue() * 1.3);
            } else {
                this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(BALANCE.MOBPROP.VAMPIRE_MOB_DEFAULT_DMG);
            }
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(entityCreature.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue() * 1.5);
            this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(entityCreature.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getBaseValue());
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(entityCreature.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue() * 1.2);
        } else {
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(1000);
            this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(0);
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0);
        }

    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("entity")) {
            entityCreature = (EntityCreature) EntityList.createEntityFromNBT(nbt.getCompoundTag("entity"), worldObj);
            entityChanged = true;
            setSize(entityCreature.width, entityCreature.height);
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!worldObj.isRemote && entityCreature == null) {
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
//            entityCreature.chunkCoordX=this.chunkCoordX;
//            entityCreature.chunkCoordY=this.chunkCoordY;
//            entityCreature.chunkCoordZ=this.chunkCoordZ;
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
            entityCreature = (EntityCreature) EntityList.createEntityFromNBT(nbt.getCompoundTag("entity"), worldObj);
            setSize(entityCreature.width, entityCreature.height);
            entityChanged = true;
        }
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

    protected boolean nil() {
        return entityCreature == null;
    }
}
