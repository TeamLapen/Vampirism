package de.teamlapen.vampirism.entity.vampire;

import com.mojang.authlib.GameProfile;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.VampireBookLootProvider;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.vampire.IAdvancedVampire;
import de.teamlapen.vampirism.api.settings.Supporter;
import de.teamlapen.vampirism.api.world.ICaptureAttributes;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.entity.action.ActionHandlerEntity;
import de.teamlapen.vampirism.entity.ai.goals.*;
import de.teamlapen.vampirism.entity.hunter.HunterBaseEntity;
import de.teamlapen.vampirism.util.IPlayerOverlay;
import de.teamlapen.vampirism.util.PlayerModelType;
import de.teamlapen.vampirism.util.SupporterManager;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Advanced vampire. Is strong. Represents supporters
 */
public class AdvancedVampireEntity extends VampireBaseEntity implements IAdvancedVampire, IPlayerOverlay, IEntityActionUser, VampireBookLootProvider {
    private static final EntityDataAccessor<Integer> LEVEL = SynchedEntityData.defineId(AdvancedVampireEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(AdvancedVampireEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> NAME = SynchedEntityData.defineId(AdvancedVampireEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(AdvancedVampireEntity.class, EntityDataSerializers.STRING);

    public static AttributeSupplier.@NotNull Builder getAttributeBuilder() {
        return VampireBaseEntity.getAttributeBuilder()
                .add(Attributes.MAX_HEALTH, BalanceMobProps.mobProps.ADVANCED_VAMPIRE_MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BalanceMobProps.mobProps.ADVANCED_VAMPIRE_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BalanceMobProps.mobProps.ADVANCED_VAMPIRE_SPEED)
                .add(Attributes.FOLLOW_RANGE, 13);
    }

    private final int MAX_LEVEL = 1;
    /**
     * available actions for AI task & task
     */
    private final @NotNull ActionHandlerEntity<?> entityActionHandler;
    private final EntityClassType entityclass;
    private final @NotNull EntityActionTier entitytier;
    /**
     * Store the approximate count of entities that are following this advanced vampire.
     * Not guaranteed to be exact and not saved to nbt
     */
    private int followingEntities = 0;
    /**
     * Overlay player texture and if slim (true)
     */
    @Nullable
    private Pair<ResourceLocation, PlayerModelType> skinDetails;
    /**
     * If set, the vampire book with this id should be dropped
     */
    @Nullable
    private String lootBookId;
    //Village stuff ----------------------------------------------------------------------------------------------------
    @Nullable
    private ICaptureAttributes villageAttributes;
    private boolean attack;

    public AdvancedVampireEntity(EntityType<? extends AdvancedVampireEntity> type, Level world) {
        super(type, world, true);
        this.canSuckBloodFromPlayer = true;
        this.setSpawnRestriction(SpawnRestriction.SPECIAL);
        this.setDontDropEquipment();
        entitytier = EntityActionTier.High;
        entityclass = EntityClassType.getRandomClass(this.getRandom());
        IEntityActionUser.applyAttributes(this);
        this.entityActionHandler = new ActionHandlerEntity<>(this);
        this.enableImobConversion();
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("level", getEntityLevel());
        nbt.putInt("type", getEyeType());
        nbt.putString("texture", getEntityData().get(TEXTURE));
        nbt.putString("name", getEntityData().get(NAME));
        nbt.putInt("entityclasstype", EntityClassType.getID(entityclass));
        if (entityActionHandler != null) {
            entityActionHandler.write(nbt);
        }
        nbt.putBoolean("attack", this.attack);
        if (lootBookId != null) {
            nbt.putString("lootBookId", lootBookId);
        }
    }

    @Override
    public void attackVillage(ICaptureAttributes totem) {
        this.villageAttributes = totem;
        this.attack = true;
    }

    @Override
    public void decreaseFollowerCount() {
        followingEntities = Math.max(0, followingEntities - 1);
    }

    @Override
    public void defendVillage(ICaptureAttributes totem) {
        this.villageAttributes = totem;
        this.attack = false;
    }

    @Override
    public ActionHandlerEntity<?> getActionHandler() {
        return entityActionHandler;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (entityActionHandler != null) {
            entityActionHandler.handle();
        }
    }

    @Override
    public @NotNull Optional<String> getBookLootId() {
        return Optional.ofNullable(lootBookId);
    }

    @Nullable
    @Override
    public ICaptureAttributes getCaptureInfo() {
        return villageAttributes;
    }

    @Override
    public EntityClassType getEntityClass() {
        return entityclass;
    }

    @Override
    public EntityActionTier getEntityTier() {
        return entitytier;
    }

    @Override
    public int getVampireType() {
        return getEntityData().get(TYPE);
    }

    @Override
    public int getFollowingCount() {
        return followingEntities;
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
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1000000, 0, false, false));
            }
        }
    }

    @Override
    public int getMaxFollowerCount() {
        return BalanceMobProps.mobProps.ADVANCED_VAMPIRE_MAX_FOLLOWER;
    }

    @Override
    public int getMaxEntityLevel() {
        return MAX_LEVEL;
    }

    @NotNull
    @Override
    public Component getName() {
        String senderName = this.getEntityData().get(NAME);
        return "none".equals(senderName) ? super.getName() : Component.literal(senderName);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public @NotNull Optional<Pair<ResourceLocation, PlayerModelType>> getOverlayPlayerProperties() {
        if (skinDetails == null) {
            String name = getTextureName();
            if (name == null) return Optional.empty();
            VampirismMod.proxy.obtainPlayerSkins(new GameProfile(Util.NIL_UUID, name), p -> this.skinDetails = p);
            skinDetails = PENDING_PROP;
        }
        return Optional.of(skinDetails);
    }

    @Nullable
    @Override
    public AABB getTargetVillageArea() {
        return villageAttributes == null ? null : villageAttributes.getVillageArea();
    }

    @Nullable
    public String getTextureName() {
        String texture = this.getEntityData().get(TEXTURE);
        return "none".equals(texture) ? null : texture;
    }

    @Override
    public boolean increaseFollowerCount() {
        if (followingEntities < getMaxFollowerCount()) {
            followingEntities++;
            return true;
        }
        return false;
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
    public boolean hurt(@NotNull DamageSource damageSource, float amount) {
        boolean flag = super.hurt(damageSource, amount);
        if (flag && damageSource.getEntity() instanceof Player && this.random.nextInt(4) == 0) {
            this.addEffect(new MobEffectInstance(ModEffects.SUNSCREEN.get(), 150, 2));
        }
        return flag;
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tagCompund) {
        super.readAdditionalSaveData(tagCompund);
        if (tagCompund.contains("level")) {
            setEntityLevel(tagCompund.getInt("level"));
        }
        if (tagCompund.contains("type")) {
            getEntityData().set(TYPE, tagCompund.getInt("type"));
            getEntityData().set(NAME, tagCompund.getString("name"));
            getEntityData().set(TEXTURE, tagCompund.getString("texture"));
        }
        if (entityActionHandler != null) {
            entityActionHandler.read(tagCompund);
        }
        if (tagCompund.contains("attack")) {
            this.attack = tagCompund.getBoolean("attack");
        }
        if (tagCompund.contains("lootBookId")) {
            this.lootBookId = tagCompund.getString("lootBookId");
        }
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
    protected float calculateFireDamage(float amount) {
        return (float) (amount * BalanceMobProps.mobProps.ADVANCED_VAMPIRE_FIRE_VULNERABILITY);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(LEVEL, -1);
        this.getEntityData().define(TYPE, 0);
        this.getEntityData().define(NAME, "none" );
        this.getEntityData().define(TEXTURE, "none" );

    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        Supporter supporter = SupporterManager.getRandomVampire(random);
        lootBookId = supporter.bookId();
        this.getEntityData().set(TYPE, createCustomisationFlag(supporter));
        this.getEntityData().set(NAME, supporter.name());
        this.getEntityData().set(TEXTURE, supporter.texture());
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    private static int createCustomisationFlag(Supporter supporter) {
        Map<String, String> appearance = supporter.appearance();
        int type = 0;
        type |= (Integer.parseInt(appearance.getOrDefault("eye", "0")) & 0b111111);
        type |= (Integer.parseInt(appearance.getOrDefault("fang", "6"))  & 0b111111) << 6;
        type |= (Integer.parseInt(appearance.getOrDefault("body", "16")) & 0b11111111) << 12;
        return type;
    }

    @Override
    protected @NotNull EntityType<?> getIMobTypeOpt(boolean iMob) {
        return iMob ? ModEntities.ADVANCED_VAMPIRE_IMOB.get() : ModEntities.ADVANCED_VAMPIRE.get();
    }

    @Override
    public int getExperienceReward() {
        return 10 * (1 + getEntityLevel());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new BreakDoorGoal(this, (difficulty) -> difficulty == net.minecraft.world.Difficulty.HARD));//Only break doors on hard difficulty
        this.goalSelector.addGoal(2, new RestrictSunVampireGoal<>(this));
        this.goalSelector.addGoal(3, new FleeSunVampireGoal<>(this, 0.9, false));
        this.goalSelector.addGoal(3, new FleeGarlicVampireGoal(this, 0.9, false));
        this.goalSelector.addGoal(4, new AttackMeleeNoSunGoal(this, 1.0, false));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.9, 25));
        this.goalSelector.addGoal(9, new LookAtClosestVisibleGoal(this, Player.class, 13F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, HunterBaseEntity.class, 17F));
        this.goalSelector.addGoal(11, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new AttackVillageGoal<>(this));
        this.targetSelector.addGoal(2, new DefendVillageGoal<>(this));//Should automatically be mutually exclusive with  attack village
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, true, false, null)));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, PathfinderMob.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, PatrollingMonster.class, 5, true, true, (living) -> UtilLib.isInsideStructure(living, StructureTags.VILLAGE)));
    }

    protected void updateEntityAttributes() {
        int l = Math.max(getEntityLevel(), 0);
        Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(BalanceMobProps.mobProps.ADVANCED_VAMPIRE_MAX_HEALTH + BalanceMobProps.mobProps.ADVANCED_VAMPIRE_MAX_HEALTH_PL * l);
        Objects.requireNonNull(this.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(BalanceMobProps.mobProps.ADVANCED_VAMPIRE_ATTACK_DAMAGE + BalanceMobProps.mobProps.ADVANCED_VAMPIRE_ATTACK_DAMAGE_PL * l);
    }

    public static class IMob extends AdvancedVampireEntity implements net.minecraft.world.entity.monster.Enemy {

        public IMob(EntityType<? extends AdvancedVampireEntity> type, Level world) {
            super(type, world);
        }

    }
}
