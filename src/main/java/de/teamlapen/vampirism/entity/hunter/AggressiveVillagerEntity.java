package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IAggressiveVillager;
import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import de.teamlapen.vampirism.api.entity.hunter.IHunterMob;
import de.teamlapen.vampirism.api.world.IVillageAttributes;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.VampirismVillagerEntity;
import de.teamlapen.vampirism.entity.goals.DefendVillageGoal;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Villager that is equipped with a fork and hunts vampires
 */
public class AggressiveVillagerEntity extends VampirismVillagerEntity implements IHunterMob, IAggressiveVillager, IVillageCaptureEntity {
    /**
     * Creates a hunter villager as an copy to the given villager
     *
     * @param villager Is not modified
     * @return
     */
    public static AggressiveVillagerEntity makeHunter(@Nonnull VillagerEntity villager) {
        AggressiveVillagerEntity hunter = ModEntities.villager_angry.create(villager.world);
        CompoundNBT nbt = new CompoundNBT();
        villager.writeWithoutTypeId(nbt);
        hunter.read(nbt);
        hunter.setUniqueId(MathHelper.getRandomUUID(hunter.rand));
        hunter.setHeldItem(Hand.MAIN_HAND, new ItemStack(ModItems.pitchfork));
        return hunter;
    }

    public AggressiveVillagerEntity(EntityType<? extends AggressiveVillagerEntity> type, World worldIn) {
        super(type, worldIn);
        ((GroundPathNavigator) getNavigator()).setBreakDoors(true);
    }

    @Override
    public LivingEntity getRepresentingEntity() {
        return this;
    }

    @Override
    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        ILivingEntityData data = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(ModItems.pitchfork));
        return data;
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(Balance.mobProps.HUNTER_VILLAGER_ATTACK_DAMAGE);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Balance.mobProps.HUNTER_VILLAGER_MAX_HEALTH);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new MeleeAttackGoal(this, 0.6, false));
        this.goalSelector.addGoal(8, new MoveThroughVillageGoal(this, 0.55, false, 400, () -> true));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<PlayerEntity>(this, PlayerEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, false, false, null)));
        this.targetSelector.addGoal(3, new DefendVillageGoal<>(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<CreatureEntity>(this, CreatureEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)) {

            @Override
            protected double getTargetDistance() {
                return super.getTargetDistance() / 2;
            }
        });
    }

    @Override
    protected void initBrain(Brain<VillagerEntity> p_213744_1_) {
    }

    @Override
    public void resetBrain(ServerWorld p_213770_1_) {
    }

    //Village capture---------------------------------------------------------------------------------------------------
    private IVillageAttributes villageAttributes;
    @Override
    public void stopVillageAttackDefense() {
        VillagerEntity villager = EntityType.VILLAGER.create(this.world);
        this.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
        CompoundNBT nbt = new CompoundNBT();
        this.writeWithoutTypeId(nbt);
        villager.read(nbt);
        villager.setUniqueId(MathHelper.getRandomUUID(this.rand));
        world.addEntity(villager);
        this.remove();
    }

    @Override
    public boolean isAttackingVillage() {
        return false;
    }

    @Override
    public boolean isDefendingVillage() {
        return villageAttributes != null;
    }

    @Override
    public void attackVillage(IVillageAttributes villageAttributes) {
        this.villageAttributes = villageAttributes;
    }

    @Override
    public void defendVillage(IVillageAttributes villageAttributes) {
        this.villageAttributes = villageAttributes;
    }

    @Nullable
    @Override
    public IVillageAttributes getVillageAttributes() {
        return villageAttributes;
    }

    @Override
    public AxisAlignedBB getTargetVillageArea() {
        return villageAttributes.getVillageArea();
    }
}
