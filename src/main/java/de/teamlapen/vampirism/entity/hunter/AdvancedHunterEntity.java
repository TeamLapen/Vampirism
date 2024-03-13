package de.teamlapen.vampirism.entity.hunter;

import com.mojang.authlib.GameProfile;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.VampireBookLootProvider;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.hunter.IAdvancedHunter;
import de.teamlapen.vampirism.api.entity.hunter.IVampirismCrossbowUser;
import de.teamlapen.vampirism.api.items.ICrossbow;
import de.teamlapen.vampirism.api.items.IHunterCrossbow;
import de.teamlapen.vampirism.api.items.IVampirismCrossbow;
import de.teamlapen.vampirism.api.settings.Supporter;
import de.teamlapen.vampirism.api.world.ICaptureAttributes;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.action.ActionHandlerEntity;
import de.teamlapen.vampirism.entity.ai.goals.AttackRangedCrossbowGoal;
import de.teamlapen.vampirism.entity.ai.goals.AttackVillageGoal;
import de.teamlapen.vampirism.entity.ai.goals.DefendVillageGoal;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;

/**
 * Advanced hunter. Is strong. Represents supporters
 */
public class AdvancedHunterEntity extends HunterBaseEntity implements IAdvancedHunter, IPlayerOverlay, IEntityActionUser, VampireBookLootProvider, IVampirismCrossbowUser {
    private static final EntityDataAccessor<Integer> LEVEL = SynchedEntityData.defineId(AdvancedHunterEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(AdvancedHunterEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> NAME = SynchedEntityData.defineId(AdvancedHunterEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(AdvancedHunterEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(AdvancedHunterEntity.class, EntityDataSerializers.BOOLEAN);

    private static final int MAX_LEVEL = 1;
    private static final int MOVE_TO_RESTRICT_PRIO = 3;

    public static AttributeSupplier.@NotNull Builder getAttributeBuilder() {
        return VampirismEntity.getAttributeBuilder()
                .add(Attributes.MAX_HEALTH, BalanceMobProps.mobProps.ADVANCED_HUNTER_MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BalanceMobProps.mobProps.ADVANCED_HUNTER_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BalanceMobProps.mobProps.ADVANCED_HUNTER_SPEED);
    }

    /**
     * available actions for AI task & task
     */
    private final @NotNull ActionHandlerEntity<?> entityActionHandler;
    private final EntityClassType entityclass;
    private final @NotNull EntityActionTier entitytier;
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
    //Village capture --------------------------------------------------------------------------------------------------
    private boolean attack;
    @Nullable
    private ICaptureAttributes villageAttributes;

    public AdvancedHunterEntity(EntityType<? extends AdvancedHunterEntity> type, Level world) {
        super(type, world, true);
        saveHome = true;
        ((GroundPathNavigation) this.getNavigation()).setCanOpenDoors(true);


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
        nbt.putInt("type", getHunterType());
        nbt.putString("texture", getEntityData().get(TEXTURE));
        nbt.putString("name", getEntityData().get(NAME));
        nbt.putInt("entityclasstype", EntityClassType.getID(entityclass));
        if (entityActionHandler != null) {
            entityActionHandler.write(nbt);
        }
        nbt.putBoolean("attack", attack);
        if (lootBookId != null) {
            nbt.putString("lootBookId", lootBookId);
        }
    }

    @Override
    public void attackVillage(ICaptureAttributes attributes) {
        this.villageAttributes = attributes;
        this.attack = true;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (entityActionHandler != null) {
            entityActionHandler.handle();
        }
    }

    @Override
    public void defendVillage(ICaptureAttributes attributes) {
        this.villageAttributes = attributes;
        this.attack = false;
    }

    @Override
    public ActionHandlerEntity<?> getActionHandler() {
        return entityActionHandler;
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entity) {
        boolean flag = super.doHurtTarget(entity);
        if (flag && this.getMainHandItem().isEmpty()) {
            this.swing(InteractionHand.MAIN_HAND);  //Swing stake if nothing else is held
        }
        return flag;
    }

    @Override
    public @NotNull Optional<String> getBookLootId() {
        return Optional.ofNullable(lootBookId);
    }

    @Nullable
    @Override
    public ICaptureAttributes getCaptureInfo() {
        return this.villageAttributes;
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
    public int getHunterType() {
        return this.getEntityData().get(TYPE);
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
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1000000, 1, false, false));
            }

        }
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
    @Override
    public String getTextureName() {
        String texture = this.getEntityData().get(TEXTURE);
        return "none".equals(texture) ? null : texture;
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
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(LEVEL, -1);
        this.getEntityData().define(TYPE, 0);
        this.getEntityData().define(NAME, "none");
        this.getEntityData().define(TEXTURE, "none");
        this.getEntityData().define(IS_CHARGING_CROSSBOW, false);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        Supporter supporter = SupporterManager.getRandomHunter(random);
        this.getEntityData().set(TYPE, createCustomisationFlag(supporter));
        this.getEntityData().set(NAME, supporter.name());
        this.getEntityData().set(TEXTURE, supporter.texture());
        this.lootBookId = supporter.bookId();
        applyCustomisationItems(supporter);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    private void applyCustomisationItems(Supporter supporter) {
        Map<String, String> appearance = supporter.appearance();
        EquipmentType equipment = Optional.ofNullable(appearance.get("equipment")).map(EquipmentType::get).orElseGet(() -> {
            EquipmentType[] types = EquipmentType.values();
            return types[random.nextInt(types.length)];
        });
        this.setLeftHanded(false);
        this.setItemSlot(EquipmentSlot.MAINHAND, equipment.getMainHand());
        this.setItemSlot(EquipmentSlot.OFFHAND, equipment.getOffHand());

        HatType hat = Optional.ofNullable(appearance.get("hat")).map(HatType::get).orElseGet(() -> {
            HatType[] types = HatType.values();
            return types[random.nextInt(types.length)];
        });
        this.setItemSlot(EquipmentSlot.HEAD, hat.getHeadItem());
        this.setDontDropEquipment();
    }

    private static int createCustomisationFlag(Supporter supporter) {
        Map<String, String> appearance = supporter.appearance();
        int type = 0;
        type |= (Boolean.parseBoolean(appearance.getOrDefault("hasCloak", "true")) ? 1 : 0) & 0b1;
        type |= (Integer.parseInt(appearance.getOrDefault("body", "13")) & 0b11111111) << 1;
        return type;
    }

    @Override
    public int getExperienceReward() {
        return 10 * (1 + getEntityLevel());
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
            return net.neoforged.neoforge.common.CommonHooks.getProjectile(this, stack, ModItems.CROSSBOW_ARROW_NORMAL.get().getDefaultInstance());
        }
        return super.getProjectile(stack);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(2, new AttackRangedCrossbowGoal<>(this, 0.8, 100));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0, false));

        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 0.7, 50));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 13F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, VampireBaseEntity.class, 17F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new AttackVillageGoal<>(this));
        this.targetSelector.addGoal(2, new DefendVillageGoal<>(this));//Should automatically be mutually exclusive with  attack village
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, false, false, null)));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, PathfinderMob.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Zombie.class, true, true));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, PatrollingMonster.class, 5, true, true, (living) -> UtilLib.isInsideStructure(living, StructureTags.VILLAGE)));
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
    public void shootCrossbowProjectile(@NotNull LivingEntity pTarget, @NotNull ItemStack pCrossbowStack, @NotNull Projectile pProjectile, float pProjectileAngle) {
        this.shootCrossbowProjectile(this, pTarget, pProjectile, pProjectileAngle, 1.6f);
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
