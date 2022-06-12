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
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
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
        AggressiveVillagerEntity hunter = ModEntities.VILLAGER_ANGRY.get().create(villager.level);
        assert hunter != null;
        CompoundNBT nbt = new CompoundNBT();
        if (villager.isSleeping()) {
            villager.stopSleeping();
        }
        villager.saveWithoutId(nbt);
        hunter.load(nbt);
        hunter.setUUID(MathHelper.createInsecureUUID(hunter.random));
        hunter.setItemInHand(Hand.MAIN_HAND, new ItemStack(ModItems.PITCHFORK.get()));
        return hunter;
    }

    public static AttributeModifierMap.MutableAttribute getAttributeBuilder() {
        return VampirismVillagerEntity.getAttributeBuilder()
                .add(Attributes.MAX_HEALTH, BalanceMobProps.mobProps.HUNTER_VILLAGER_MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BalanceMobProps.mobProps.HUNTER_VILLAGER_ATTACK_DAMAGE)
                .add(Attributes.FOLLOW_RANGE, 32);
    }
    //Village capture---------------------------------------------------------------------------------------------------
    @Nullable
    private ICaptureAttributes villageAttributes;

    public AggressiveVillagerEntity(EntityType<? extends AggressiveVillagerEntity> type, World worldIn) {
        super(type, worldIn);
        ((GroundPathNavigator) getNavigation()).setCanOpenDoors(true);
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
    public LivingEntity getRepresentingEntity() {
        return this;
    }

    @Nullable
    @Override
    public AxisAlignedBB getTargetVillageArea() {
        return villageAttributes == null ? null : villageAttributes.getVillageArea();
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
    public ILivingEntityData finalizeSpawn(@Nonnull IServerWorld worldIn, @Nonnull DifficultyInstance difficultyIn, @Nonnull SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        ILivingEntityData data = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(ModItems.PITCHFORK.get()));
        return data;
    }

    @Override
    public void refreshBrain(@Nonnull ServerWorld serverWorldIn) {
    }

    @Override
    public void stopVillageAttackDefense() {
        VillagerEntity villager = EntityType.VILLAGER.create(this.level);
        assert villager != null;
        this.setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        CompoundNBT nbt = new CompoundNBT();
        this.saveWithoutId(nbt);
        villager.load(nbt);
        villager.setUUID(MathHelper.createInsecureUUID(this.random));
        UtilLib.replaceEntity(this, villager);
    }

    @Override
    protected ITextComponent getTypeName() {
        return this.getType().getDescription(); //Don't use profession as part of the translation key
    }

    @Override
    protected void registerBrainGoals(Brain<VillagerEntity> brainIn) {
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
            protected double getFollowDistance() {
                return super.getFollowDistance() / 2;
            }
        });
    }
}
