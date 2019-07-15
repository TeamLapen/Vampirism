package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.entity.goals.AttackMeleeNoSunGoal;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Converted creature class.
 * Contains (stores and syncs) a normal Entity for rendering purpose
 */
public class ConvertedCreatureEntity<T extends CreatureEntity> extends VampireBaseEntity implements IConvertedCreature<T>, ISyncable {
    private final static Logger LOGGER = LogManager.getLogger(ConvertedCreatureEntity.class);
    private T entityCreature;
    private boolean entityChanged = false;
    private boolean canDespawn = false;

    public ConvertedCreatureEntity(EntityType<? extends CreatureEntity> type, World world) {
        super(type, world, false);

    }

    @Override
    public ITextComponent getName() {
        return new StringTextComponent(new TranslationTextComponent("entity.vampirism.vampire.name") + " " + (nil() ? super.getName() : entityCreature.getName()));
    }

    public T getOldCreature() {
        return entityCreature;
    }

    @Override
    public void loadUpdateFromNBT(CompoundNBT nbt) {
        if (nbt.contains("entity_old")) {
            setEntityCreature((T) EntityType.loadEntityUnchecked(nbt.getCompound("entity_old"), getEntityWorld()).orElse(null));
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
            entityCreature.setMotion(this.getMotion());
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
    public void playAmbientSound() {
        if (!nil()) {
            entityCreature.playAmbientSound();
        }
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);
        if (nbt.contains("entity_old")) {
            setEntityCreature((T) EntityType.loadEntityUnchecked(nbt.getCompound("entity_old"), world).orElse(null));
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
                this.recalculateSize();
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
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        writeOldEntityToNBT(nbt);
        nbt.putBoolean("converter_canDespawn", canDespawn);

    }

    @Override
    public void writeFullUpdateToNBT(CompoundNBT nbt) {
        writeOldEntityToNBT(nbt);

    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.updateEntityAttributes();
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return super.canDespawn(distanceToClosestPlayer) && canDespawn;
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
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new AvoidEntityGoal<CreatureEntity>(this, CreatureEntity.class, 10, 1.0, 1.1, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, VReference.HUNTER_FACTION)));
        //this.tasks.addTask(3, new FleeSunVampireGoal(this, 1F));
        this.goalSelector.addGoal(4, new RestrictSunGoal(this));
        this.goalSelector.addGoal(5, new AttackMeleeNoSunGoal(this, 0.9D, false));
        this.experienceValue = 2;

        this.goalSelector.addGoal(11, new RandomWalkingGoal(this, 0.7));
        this.goalSelector.addGoal(13, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(15, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<PlayerEntity>(this, PlayerEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, true, false, null)));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<CreatureEntity>(this, CreatureEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)));
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
    private void writeOldEntityToNBT(CompoundNBT nbt) {
        if (!nil()) {
            try {
                CompoundNBT entity = new CompoundNBT();
                entityCreature.removed = false;
                entityCreature.writeUnlessPassenger(entity);
                entityCreature.removed = true;
                nbt.put("entity_old", entity);
            } catch (Exception e) {
                LOGGER.error(String.format("Failed to write old entity (%s) to NBT. If this happens more often please report this to the mod author.", entityCreature), e);
                this.setEntityCreature(null);
            }
        }

    }
}
