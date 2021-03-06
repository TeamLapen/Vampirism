package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IAggressiveVillager;
import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import de.teamlapen.vampirism.api.entity.hunter.IHunterMob;
import de.teamlapen.vampirism.api.world.ICaptureAttributes;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.VampirismVillagerEntity;
import de.teamlapen.vampirism.entity.goals.DefendVillageGoal;
import de.teamlapen.vampirism.util.SharedMonsterAttributes;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
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
     * @param villager Is not modified or removed
     */
    public static AggressiveVillagerEntity makeHunter(@Nonnull VillagerEntity villager) {
        AggressiveVillagerEntity hunter = ModEntities.villager_angry.create(villager.world);
        assert hunter != null;
        CompoundNBT nbt = new CompoundNBT();
        if (villager.isSleeping()) {
            villager.wakeUp();
        }
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

    //Village capture---------------------------------------------------------------------------------------------------
    @Nullable
    private ICaptureAttributes villageAttributes;


    public static AttributeModifierMap.MutableAttribute getAttributeBuilder() {
        return VampirismVillagerEntity.getAttributeBuilder()
                .createMutableAttribute(SharedMonsterAttributes.MAX_HEALTH, BalanceMobProps.mobProps.HUNTER_VILLAGER_MAX_HEALTH)
                .createMutableAttribute(SharedMonsterAttributes.ATTACK_DAMAGE, BalanceMobProps.mobProps.HUNTER_VILLAGER_ATTACK_DAMAGE)
                .createMutableAttribute(SharedMonsterAttributes.FOLLOW_RANGE, 32);
    }

    @Override
    public void attackVillage(ICaptureAttributes villageAttributes) {
        this.villageAttributes = villageAttributes;
    }

    @Override
    public void defendVillage(ICaptureAttributes villageAttributes) {
        this.villageAttributes = villageAttributes;
    }

    @Nullable
    @Override
    public ICaptureAttributes getCaptureInfo() {
        return villageAttributes;
    }

    @Override
    protected ITextComponent getProfessionName() {
        return this.getType().getName(); //Don't use profession as part of the translation key
    }

    @Nullable
    @Override
    public AxisAlignedBB getTargetVillageArea() {
        return villageAttributes == null ? null : villageAttributes.getVillageArea();
    }

    @Override
    public ILivingEntityData onInitialSpawn(@Nonnull IServerWorld worldIn, @Nonnull DifficultyInstance difficultyIn, @Nonnull SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        ILivingEntityData data = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(ModItems.pitchfork));
        return data;
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
    public void resetBrain(@Nonnull ServerWorld serverWorldIn) {
    }

    @Override
    public void stopVillageAttackDefense() {
        VillagerEntity villager = EntityType.VILLAGER.create(this.world);
        assert villager != null;
        this.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
        CompoundNBT nbt = new CompoundNBT();
        this.writeWithoutTypeId(nbt);
        villager.read(nbt);
        villager.setUniqueId(MathHelper.getRandomUUID(this.rand));
        UtilLib.replaceEntity(this, villager);
    }

    @Override
    protected void initBrain(Brain<VillagerEntity> brainIn) {
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new MeleeAttackGoal(this, 0.6, false));
        this.goalSelector.addGoal(8, new MoveThroughVillageGoal(this, 0.55, false, 400, () -> true));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, false, false, null)));
        this.targetSelector.addGoal(3, new DefendVillageGoal<>(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<CreatureEntity>(this, CreatureEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)) {

            @Override
            protected double getTargetDistance() {
                return super.getTargetDistance() / 2;
            }
        });
    }
}
