package de.teamlapen.vampirism.entity.minions.vampire;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.minions.IMinionCommand;
import de.teamlapen.vampirism.api.entity.minions.ISaveableMinion;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMinion;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.minions.ai.HurtByTargetMinionGoal;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.util.MinionHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Base class for small vampire minions
 * Does not count as entity for spawning algorithm
 */
public abstract class VampireMinionBaseEntity extends VampireBaseEntity implements IVampireMinion {


    /**
     * Datamanager key for oldVampireTexture
     * Used for the visual transition from normal vampire to players minion
     */
    private final static DataParameter<Integer> TEXTURE = EntityDataManager.createKey(VampireMinionBaseEntity.class, DataSerializers.VARINT);


    private IMinionCommand activeCommand;
    private boolean wantsBlood = false;


    public VampireMinionBaseEntity(EntityType type, World world) {
        super(type, world, false);
        //this.func_110163_bv(); TODO check if this was relevant

        activeCommand = this.createDefaultCommand();
        activeCommand.onActivated();
    }

    @Override
    public void activateMinionCommand(IMinionCommand command) {
        if (command == null)
            return;
        this.activeCommand.onDeactivated();
        this.activeCommand = command;
        this.activeCommand.onActivated();
    }

    @Override
    public boolean canAttack(EntityType<?> typeIn) {
        //if(typeIn.equals(ModEntities.portal_guard) return false;
        return super.canAttack(typeIn);
    }

    public IMinionCommand getActiveCommand() {
        return this.activeCommand;
    }

    @Override
    public float getBlockPathWeight(BlockPos pos) {
        float i = 0.5F - this.world.getLight(pos);
        if (i > 0)
            return i;
        return 0.01F;
    }

    public int getOldVampireTexture() {
        return getDataManager().get(TEXTURE);
    }

    public void setOldVampireTexture(int oldVampireTexture) {
        getDataManager().set(TEXTURE, oldVampireTexture);
    }

    @Override
    public int getTalkInterval() {
        return 2000;
    }

    @Override
    public boolean isChild() {
        return true;
    }

    @Override
    public void livingTick() {
        if (getOldVampireTexture() != -1 && this.ticksExisted > 50) {
            setOldVampireTexture(-1);
        }
        if (getOldVampireTexture() != -1 && world.isRemote) {
            UtilLib.spawnParticlesAroundEntity(this, ParticleTypes.WITCH, 1.0F, 3);
        }
        if (!this.world.isRemote && !this.dead) {

            List<ItemEntity> list = this.world.getEntitiesWithinAABB(ItemEntity.class, this.getBoundingBox().grow(1.0D, 0.0D, 1.0D));

            for (ItemEntity entityitem : list) {
                if (entityitem.isAlive() && !(entityitem.getItem().isEmpty())) {
                    ItemStack itemstack = entityitem.getItem();
                    if (activeCommand.shouldPickupItem(itemstack)) {
                        ItemStack stack1 = this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
                        if (!stack1.isEmpty()) {
                            this.entityDropItem(stack1, 0.0F);
                        }
                        this.setItemStackToSlot(EquipmentSlotType.MAINHAND, itemstack);
                        entityitem.remove();
                    }

                }
            }
        }
        if (Balance.mobProps.VAMPIRE_MINION_REGENERATE_SECS >= 0 && this.ticksExisted % (Balance.mobProps.VAMPIRE_MINION_REGENERATE_SECS * 20) == 0 && (this.getLastAttackedEntityTime() == 0 || this.getLastAttackedEntityTime() - ticksExisted > 100)) {
            this.heal(2F);
        }
        super.livingTick();
    }

    @Override
    public void onKillEntity(LivingEntity entity) {


        if (this.getLord() != null && this.getLord() instanceof VampireBaronEntity) {
            ((VampireBaronEntity) this.getLord()).onKillEntity(entity);
        } else {
            super.onKillEntity(entity);
        }

    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);
        IMinionCommand command = this.getCommand(nbt.getInt("command_id"));
        if (command != null) {
            this.activateMinionCommand(command);
        }
        if (nbt.contains("CustomName", 8) && nbt.getString("CustomName").length() > 0) {
            this.tryToSetName(new StringTextComponent(nbt.getString("CustomName")), null);
        }
    }

    /**
     * Does not nothing, since minions should not be named normaly. Use {@link #tryToSetName(ITextComponent, PlayerEntity)} instead
     */
    @Override
    public void setCustomName(@Nullable ITextComponent name) {
    }

    public void setWantsBlood(boolean wantsBlood) {
        this.wantsBlood = wantsBlood;
    }

    /**
     * Replaces {@link #setCustomName(ITextComponent)}.
     *
     * @param name
     * @param player If this isn't null, checks if the player is the minions lord
     * @return success
     */
    public boolean tryToSetName(ITextComponent name, @Nullable PlayerEntity player) {
        if (player == null || MinionHelper.isLordSafe(this, player)) {
            super.setCustomName(name);
            return true;
        }
        return false;
    }

    @Override
    public boolean wantsBlood() {
        return wantsBlood;
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        nbt.putInt("command_id", getActiveCommand().getId());
    }

    @Override
    public boolean writeUnlessPassenger(CompoundNBT nbt) {
        if (this instanceof ISaveableMinion) {
            return false;
        }
        return super.writeUnlessPassenger(nbt);
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(30D);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Balance.mobProps.VAMPIRE_MINION_MAX_HEALTH);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(Balance.mobProps.VAMPIRE_MINION_ATTACK_DAMAGE);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(Balance.mobProps.VAMPIRE_MINION_MOVEMENT_SPEED);
    }

    /**
     * Copies vampire minion data
     *
     * @param from
     */
    protected void copyDataFromMinion(VampireMinionBaseEntity from) {
        this.setOldVampireTexture(from.getOldVampireTexture());
        this.setLord(from.getLord());
        this.activateMinionCommand(from.getActiveCommand());
    }

    /**
     * Has to return the command which is activated on default
     *
     * @return
     */
    protected abstract
    @Nonnull
    IMinionCommand createDefaultCommand();

    @Override
    protected void registerData() {
        super.registerData();
        getDataManager().register(TEXTURE, -1);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(15, new RandomWalkingGoal(this, 0.7));
        this.goalSelector.addGoal(16, new LookAtGoal(this, PlayerEntity.class, 10));

        this.targetSelector.addGoal(8, new HurtByTargetMinionGoal(this, false));
    }
}
