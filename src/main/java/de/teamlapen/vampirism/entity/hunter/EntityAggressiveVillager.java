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
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.GroundPathNavigator;
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
    public static EntityAggressiveVillager makeHunter(VillagerEntity villager) {
        EntityAggressiveVillager hunter = new EntityAggressiveVillager(villager.world);
        CompoundNBT nbt = new CompoundNBT();
        villager.writeWithoutTypeId(nbt);
        hunter.read(nbt);
        hunter.setUniqueId(MathHelper.getRandomUUID(hunter.rand));
        hunter.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(ModItems.pitchfork));
        return hunter;
    }


    public EntityAggressiveVillager(World worldIn) {
        super(worldIn);
        ((GroundPathNavigator) getNavigator()).setEnterDoors(true);
    }



    @Override
    public LivingEntity getRepresentingEntity() {
        return this;
    }

    @Override
    public ILivingEntityData onInitialSpawn(DifficultyInstance difficulty, ILivingEntityData livingdata, @Nullable CompoundNBT itemNbt) {
        ILivingEntityData data = super.onInitialSpawn(difficulty, livingdata, itemNbt);
        this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(ModItems.pitchfork));
        return data;
    }

    @Override
    public void livingTick() {
        super.livingTick();
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(Balance.mobProps.HUNTER_VILLAGER_ATTACK_DAMAGE);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Balance.mobProps.HUNTER_VILLAGER_MAX_HEALTH);
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
        VillagerEntity villager = new VillagerEntity(this.world);
        CompoundNBT nbt = new CompoundNBT();
        this.writeWithoutTypeId(nbt);
        villager.read(nbt);
        villager.setUniqueId(MathHelper.getRandomUUID(this.rand));
        world.spawnEntity(villager);
        this.remove();
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.taskEntries.removeIf(entry -> entry.action instanceof TradeWithPlayerGoal || entry.action instanceof LookAtCustomerGoal || entry.action instanceof EntityAIVillagerMate || entry.action instanceof EntityAIFollowGolem);
        this.tasks.addTask(6, new MeleeAttackGoal(this, 0.6, false));
        this.tasks.addTask(8, new EntityAIMoveThroughVillageCustom(this, 0.55, false, 400));


        this.targetTasks.addTask(1, new HurtByTargetGoal(this, false));
        this.targetTasks.addTask(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, false, false, null)));
        this.targetTasks.addTask(3, new EntityAIDefendVillage<>(this));
        this.targetTasks.addTask(4, new NearestAttackableTargetGoal<CreatureEntity>(this, CreatureEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)) {

            @Override
            protected double getTargetDistance() {
                return super.getTargetDistance() / 2;
            }
        });
    }
}
