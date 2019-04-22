package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IAggressiveVillager;
import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import de.teamlapen.vampirism.api.entity.hunter.IHunterMob;
import de.teamlapen.vampirism.api.world.IVampirismVillage;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.EntityVillagerVampirism;
import de.teamlapen.vampirism.entity.ai.EntityAIDefendVillage;
import de.teamlapen.vampirism.entity.ai.EntityAIMoveThroughVillageCustom;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Villager that is equipped with a fork and hunts vampires
 */
public class EntityAggressiveVillager extends EntityVillagerVampirism implements IHunterMob, IAggressiveVillager, IVillageCaptureEntity {
    private AxisAlignedBB area;
    /**
     * Creates a hunter villager as an copy to the given villager
     *
     * @param villager Is not modified
     * @return
     */
    public static EntityAggressiveVillager makeHunter(EntityVillager villager) {
        EntityAggressiveVillager hunter = new EntityAggressiveVillager(villager.world);
        NBTTagCompound nbt = new NBTTagCompound();
        villager.writeToNBT(nbt);
        hunter.readFromNBT(nbt);
        hunter.setUniqueId(MathHelper.getRandomUUID(hunter.rand));
        hunter.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ModItems.pitchfork));
        return hunter;
    }


    public EntityAggressiveVillager(World worldIn) {
        super(worldIn);
        ((PathNavigateGround) getNavigator()).setEnterDoors(true);
    }



    @Override
    public EntityLivingBase getRepresentingEntity() {
        return this;
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        IEntityLivingData data = super.onInitialSpawn(difficulty, livingdata);
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ModItems.pitchfork));
        return data;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(Balance.mobProps.HUNTER_VILLAGER_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Balance.mobProps.HUNTER_VILLAGER_MAX_HEALTH);
    }

    @Nullable
    @Override
    public IVampirismVillage getCurrentFriendlyVillage() {
        return this.cachedVillage != null ? this.cachedVillage.getControllingFaction() == VReference.HUNTER_FACTION ? this.cachedVillage : null : null;
    }

    @Override
    public void attackVillage(AxisAlignedBB area) {
        this.area = area;
    }

    @Override
    public void defendVillage(AxisAlignedBB area) {
        this.area = area;
    }

    @Override
    public AxisAlignedBB getTargetVillageArea() {
        return area;
    }

    @Override
    public boolean isAttackingVillage() {
        return false;
    }

    @Override
    public void stopVillageAttackDefense() {
        EntityVillager villager = new EntityVillager(this.world);
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        villager.readFromNBT(nbt);
        villager.setUniqueId(MathHelper.getRandomUUID(this.rand));
        world.spawnEntity(villager);
        this.remove();
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.taskEntries.removeIf(entry -> entry.action instanceof EntityAITradePlayer || entry.action instanceof EntityAILookAtTradePlayer || entry.action instanceof EntityAIVillagerMate || entry.action instanceof EntityAIFollowGolem);
        this.tasks.addTask(6, new EntityAIAttackMelee(this, 0.6, false));
        this.tasks.addTask(8, new EntityAIMoveThroughVillageCustom(this, 0.55, false, 400));


        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, false, false, null)));
        this.targetTasks.addTask(3, new EntityAIDefendVillage<>(this));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<EntityCreature>(this, EntityCreature.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)) {

            @Override
            protected double getTargetDistance() {
                return super.getTargetDistance() / 2;
            }
        });
    }
}
