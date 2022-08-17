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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Villager that is equipped with a fork and hunts vampires
 */
public class AggressiveVillagerEntity extends VampirismVillagerEntity implements IHunterMob, IAggressiveVillager, IVillageCaptureEntity {
    /**
     * Creates a hunter villager as a copy to the given villager
     *
     * @param villager Is not modified or removed
     */
    public static AggressiveVillagerEntity makeHunter(@NotNull Villager villager) {
        AggressiveVillagerEntity hunter = ModEntities.VILLAGER_ANGRY.get().create(villager.level);
        assert hunter != null;
        CompoundTag nbt = new CompoundTag();
        if (villager.isSleeping()) {
            villager.stopSleeping();
        }
        villager.saveWithoutId(nbt);
        hunter.load(nbt);
        hunter.setUUID(Mth.createInsecureUUID(hunter.random));
        hunter.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.PITCHFORK.get()));
        return hunter;
    }

    public static AttributeSupplier.Builder getAttributeBuilder() {
        return VampirismVillagerEntity.getAttributeBuilder()
                .add(Attributes.MAX_HEALTH, BalanceMobProps.mobProps.HUNTER_VILLAGER_MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BalanceMobProps.mobProps.HUNTER_VILLAGER_ATTACK_DAMAGE)
                .add(Attributes.FOLLOW_RANGE, 32);
    }

    //Village capture---------------------------------------------------------------------------------------------------
    @Nullable
    private ICaptureAttributes villageAttributes;

    public AggressiveVillagerEntity(EntityType<? extends AggressiveVillagerEntity> type, Level worldIn) {
        super(type, worldIn);
        ((GroundPathNavigation) getNavigation()).setCanOpenDoors(true);
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
    public AABB getTargetVillageArea() {
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
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor worldIn, @NotNull DifficultyInstance difficultyIn, @NotNull MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.PITCHFORK.get()));
        return data;
    }

    @Override
    public void refreshBrain(@NotNull ServerLevel serverWorldIn) {
    }

    @Override
    public void stopVillageAttackDefense() {
        Villager villager = EntityType.VILLAGER.create(this.level);
        assert villager != null;
        this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        CompoundTag nbt = new CompoundTag();
        this.saveWithoutId(nbt);
        villager.load(nbt);
        villager.setUUID(Mth.createInsecureUUID(this.random));
        UtilLib.replaceEntity(this, villager);
    }

    @NotNull
    @Override
    protected Component getTypeName() {
        return this.getType().getDescription(); //Don't use profession as part of the translation key
    }

    @Override
    protected void registerBrainGoals(@NotNull Brain<Villager> brainIn) {
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new MeleeAttackGoal(this, 0.6, false));
        this.goalSelector.addGoal(8, new MoveThroughVillageGoal(this, 0.55, false, 400, () -> true));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, false, false, null)));
        this.targetSelector.addGoal(3, new DefendVillageGoal<>(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, PathfinderMob.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)) {

            @Override
            protected double getFollowDistance() {
                return super.getFollowDistance() / 2;
            }
        });
    }
}
