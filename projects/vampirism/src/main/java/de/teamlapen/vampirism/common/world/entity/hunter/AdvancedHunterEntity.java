package de.teamlapen.vampirism.common.world.entity.hunter;

import de.teamlapen.faction.api.factions.IFactionPredicate;
import de.teamlapen.faction.api.world.ICaptureAttributes;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.world.entity.VampireBookLootProvider;
import de.teamlapen.vampirism.api.world.entity.hunter.IAdvancedHunter;
import de.teamlapen.vampirism.api.world.entity.hunter.IVampirismCrossbowUser;
import de.teamlapen.vampirism.api.world.items.IHunterCrossbow;
import de.teamlapen.vampirism.common.config.BalanceMobProps;
import de.teamlapen.vampirism.common.core.ModAttachments;
import de.teamlapen.vampirism.common.core.ModEntities;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.util.UtilLib;
import de.teamlapen.vampirism.common.util.supporter.Supporter;
import de.teamlapen.vampirism.common.world.entity.ISupporterAppearanceConsumer;
import de.teamlapen.vampirism.common.world.entity.VampirismEntity;
import de.teamlapen.vampirism.common.world.entity.ai.goals.*;
import de.teamlapen.vampirism.common.world.entity.ai.navigation.HunterPathNavigation;
import de.teamlapen.vampirism.common.world.entity.vampire.VampireBaseEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Advanced hunter. Is strong. Represents supporters
 */
public class AdvancedHunterEntity extends HunterBaseEntity implements IAdvancedHunter, IPlayerOverlay, VampireBookLootProvider, IVampirismCrossbowUser, ISupporterAppearanceConsumer {
    private static final EntityDataAccessor<Integer> LEVEL = SynchedEntityData.defineId(AdvancedHunterEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(AdvancedHunterEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Holder<Item>> SPECIAL_ARROW = SynchedEntityData.defineId(AdvancedHunterEntity.class, ModEntities.ITEM_HOLDER.get());

    private static final int MAX_LEVEL = 1;
    private static final int MOVE_TO_RESTRICT_PRIO = 3;

    public static AttributeSupplier.@NotNull Builder getAttributeBuilder() {
        return VampirismEntity.getAttributeBuilder()
                .add(Attributes.MAX_HEALTH, BalanceMobProps.mobProps.ADVANCED_HUNTER_MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BalanceMobProps.mobProps.ADVANCED_HUNTER_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BalanceMobProps.mobProps.ADVANCED_HUNTER_SPEED);
    }

    private boolean attack;
    @Nullable
    private ICaptureAttributes villageAttributes;

    public AdvancedHunterEntity(EntityType<? extends AdvancedHunterEntity> type, Level world) {
        super(type, world);
        saveHome = true;
        this.getNavigation().setCanOpenDoors(true);


        this.setDontDropEquipment();
        this.enableImobConversion();
    }

    @Override
    public void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("level", getEntityLevel());
        output.putBoolean("attack", attack);
        output.store("specialArrow", BuiltInRegistries.ITEM.holderByNameCodec(), getEntityData().get(SPECIAL_ARROW));
    }

    @Override
    public void attackVillage(@NonNull ICaptureAttributes attributes) {
        this.villageAttributes = attributes;
        this.attack = true;
    }

    @Override
    public void defendVillage(@NonNull ICaptureAttributes attributes) {
        this.villageAttributes = attributes;
        this.attack = false;
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, @NotNull Entity entity) {
        boolean flag = super.doHurtTarget(level, entity);
        if (flag && this.getMainHandItem().isEmpty()) {
            this.swing(InteractionHand.MAIN_HAND);  //Swing stake if nothing else is held
        }
        return flag;
    }

    @Override
    public @NotNull Optional<String> getBookLootId() {
        return getData(ModAttachments.SUPPORTER).bookId();
    }

    @Nullable
    @Override
    public ICaptureAttributes getCaptureInfo() {
        return this.villageAttributes;
    }

    @Override
    public int getEntityLevel() {
        return getEntityData().get(LEVEL);
    }

    @Override
    public void setEntityLevel(int level) {
        if (level >= 0) {
            getEntityData().set(LEVEL, level);
            this.updateEntityAttributes();
            if (level == 1) {
                this.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 1000000, 1, false, false));
            }

        }
    }

    @Override
    public int getMaxEntityLevel() {
        return MAX_LEVEL;
    }

    @Nullable
    @Override
    public AABB getTargetVillageArea() {
        return villageAttributes == null ? null : villageAttributes.getVillageArea();
    }

    @Override
    public boolean isAttackingVillage() {
        return villageAttributes != null && attack;
    }

    @Override
    public boolean isDefendingVillage() {
        return villageAttributes != null && !attack;
    }

    @Override
    public boolean isLookingForHome() {
        return getHome() == null;
    }

    @Override
    public void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        input.getInt("level").ifPresent(this::setEntityLevel);
        this.attack = input.getBooleanOr("attack", false);
        input.read("specialArrow", BuiltInRegistries.ITEM.holderByNameCodec()).ifPresent(x -> this.getEntityData().set(SPECIAL_ARROW, x));
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return super.removeWhenFarAway(distanceToClosestPlayer) && isLookingForHome();
    }

    @Override
    public void setCampArea(AABB box) {
        super.setHome(box);
        this.setMoveTowardsRestriction(MOVE_TO_RESTRICT_PRIO, true);
    }

    @Override
    public void stopVillageAttackDefense() {
        this.setCustomName(null);
        this.villageAttributes = null;
    }

    @Override
    public boolean shouldShowName() {
        return true;
    }

    @Override
    public int suggestEntityLevel(@NotNull Difficulty d) {
        if (random.nextBoolean()) {
            return (int) (d.avgPercLevel() * MAX_LEVEL / 100F);
        }
        return random.nextInt(MAX_LEVEL + 1);

    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(LEVEL, -1);
        builder.define(IS_CHARGING_CROSSBOW, false);
        builder.define(SPECIAL_ARROW, ModItems.QUARREL_NORMAL);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, EntitySpawnReason pReason, @Nullable SpawnGroupData pSpawnData) {
        Supporter randomHunter = VampirismMod.services().supporterManager().getRandomHunter(random);
        setData(ModAttachments.SUPPORTER, randomHunter);
        applySupporter(this, randomHunter);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
    }

    @Override
    protected int getBaseExperienceReward(ServerLevel level) {
        this.xpReward = 10 * (1 + getEntityLevel());
        return super.getBaseExperienceReward(level);
    }

    @Override
    protected @NotNull EntityType<?> getIMobTypeOpt(boolean iMob) {
        return iMob ? ModEntities.ADVANCED_HUNTER_IMOB.get() : ModEntities.ADVANCED_HUNTER.get();
    }

    @NotNull
    @Override
    protected InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) { //processInteract
        if (hand == InteractionHand.MAIN_HAND && tryCureSanguinare(player)) return InteractionResult.SUCCESS;
        return super.mobInteract(player, hand);
    }

    @Nonnull
    @Override
    public ItemStack getProjectile(ItemStack stack) {
        if (stack.getItem() instanceof IHunterCrossbow) {
            Holder<Item> item = ModItems.QUARREL_NORMAL;
            if (random.nextFloat() < 0.2) {
                item = getEntityData().get(SPECIAL_ARROW);
            }
            return net.neoforged.neoforge.common.CommonHooks.getProjectile(this, stack, item.value().getDefaultInstance());
        }
        return super.getProjectile(stack);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new HunterPathNavigation(this, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(1, new OpenGateGoal(this, true));
        this.goalSelector.addGoal(2, new RangedHunterCrossbowAttackGoal<>(this, 0.8, 100));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0, false));

        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 0.7, 50));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 13F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, VampireBaseEntity.class, 17F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HunterHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new AttackVillageGoal<>(this));
        this.targetSelector.addGoal(2, new DefendVillageGoal<>(this));//Should automatically be mutually exclusive with  attack village
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 5, true, false, IFactionPredicate.builder(getFaction()).onlyPlayer().notNeutral().build()));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, PathfinderMob.class, 5, true, false, IFactionPredicate.builder(getFaction()).onlyNonPlayers().notNeutral().build()));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Zombie.class, true, true));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, PatrollingMonster.class, 5, true, true, (living, level) -> UtilLib.isInsideStructure(living, StructureTags.VILLAGE)));
    }

    protected void updateEntityAttributes() {
        int l = Math.max(getEntityLevel(), 0);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BalanceMobProps.mobProps.ADVANCED_HUNTER_MAX_HEALTH + BalanceMobProps.mobProps.ADVANCED_HUNTER_MAX_HEALTH_PL * l);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BalanceMobProps.mobProps.ADVANCED_HUNTER_ATTACK_DAMAGE + BalanceMobProps.mobProps.ADVANCED_HUNTER_ATTACK_DAMAGE_PL * l);
    }

    @Override
    public boolean isHoldingCrossbow() {
        return this.isHolding(stack -> stack.getItem() instanceof IHunterCrossbow);
    }

    @Override
    public boolean isChargingCrossbow() {
        return this.getEntityData().get(IS_CHARGING_CROSSBOW);
    }

    @Override
    public void setChargingCrossbow(boolean pChargingCrossbow) {
        this.getEntityData().set(IS_CHARGING_CROSSBOW, pChargingCrossbow);
    }

    @Override
    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity pTarget, float pVelocity) {
        this.performCrossbowAttack(this, 1.6f);
    }

    public static class IMob extends AdvancedHunterEntity implements net.minecraft.world.entity.monster.Enemy {

        public IMob(EntityType<? extends AdvancedHunterEntity> type, Level world) {
            super(type, world);
        }
    }
}
