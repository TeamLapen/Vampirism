package de.teamlapen.vampirism.entity.vampire;

import com.mojang.authlib.GameProfile;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.vampire.IAdvancedVampire;
import de.teamlapen.vampirism.api.world.ICaptureAttributes;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.entity.action.ActionHandlerEntity;
import de.teamlapen.vampirism.entity.goals.*;
import de.teamlapen.vampirism.entity.hunter.HunterBaseEntity;
import de.teamlapen.vampirism.util.IPlayerOverlay;
import de.teamlapen.vampirism.util.PlayerSkinHelper;
import de.teamlapen.vampirism.util.SupporterManager;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.PatrollerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

/**
 * Advanced vampire. Is strong. Represents supporters
 */
public class AdvancedVampireEntity extends VampireBaseEntity implements IAdvancedVampire, IPlayerOverlay, IEntityActionUser {
    private static final DataParameter<Integer> LEVEL = EntityDataManager.defineId(AdvancedVampireEntity.class, DataSerializers.INT);
    private static final DataParameter<Integer> TYPE = EntityDataManager.defineId(AdvancedVampireEntity.class, DataSerializers.INT);
    private static final DataParameter<String> NAME = EntityDataManager.defineId(AdvancedVampireEntity.class, DataSerializers.STRING);
    private static final DataParameter<String> TEXTURE = EntityDataManager.defineId(AdvancedVampireEntity.class, DataSerializers.STRING);

    public static AttributeModifierMap.MutableAttribute getAttributeBuilder() {
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
    private final ActionHandlerEntity<?> entityActionHandler;
    private final EntityClassType entityclass;
    private final EntityActionTier entitytier;
    /**
     * Store the approximate count of entities that are following this advanced vampire.
     * Not guaranteed to be exact and not saved to nbt
     */
    private int followingEntities = 0;
    /**
     * Overlay player texture and if slim (true)
     */
    @OnlyIn(Dist.CLIENT)
    @Nullable
    private Pair<ResourceLocation, Boolean> skinDetails;
    /**
     * If set, the vampire book with this id should be dropped
     */
    @Nullable
    private String lootBookId;
    //Village stuff ----------------------------------------------------------------------------------------------------
    @Nullable
    private ICaptureAttributes villageAttributes;
    private boolean attack;

    public AdvancedVampireEntity(EntityType<? extends AdvancedVampireEntity> type, World world) {
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
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("level", getLevel());
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
    public ActionHandlerEntity getActionHandler() {
        return entityActionHandler;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (entityActionHandler != null) {
            entityActionHandler.handle();
        }
    }

    public Optional<String> getBookLootId() {
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
    public int getEyeType() {
        return getEntityData().get(TYPE);
    }

    @Override
    public int getFollowingCount() {
        return followingEntities;
    }

    @Override
    public int getLevel() {
        return getEntityData().get(LEVEL);
    }

    @Override
    public void setLevel(int level) {
        if (level >= 0) {
            getEntityData().set(LEVEL, level);
            this.updateEntityAttributes();
            if (level == 1) {
                this.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 1000000, 0));
            }
        }
    }

    @Override
    public int getMaxFollowerCount() {
        return BalanceMobProps.mobProps.ADVANCED_VAMPIRE_MAX_FOLLOWER;
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Nonnull
    @Override
    public ITextComponent getName() {
        String senderName = this.getEntityData().get(NAME);
        return "none".equals(senderName) ? super.getName() : new StringTextComponent(senderName);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    @Nullable
    public Optional<Pair<ResourceLocation, Boolean>> getOverlayPlayerProperties() {
        if (skinDetails == null) {
            String name = getTextureName();
            if (name == null) return Optional.empty();
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PlayerSkinHelper.obtainPlayerSkinPropertiesAsync(new GameProfile(null, name), p -> this.skinDetails = p));
            skinDetails = PENDING_PROP;
        }
        return Optional.of(skinDetails);
    }

    @Nullable
    @Override
    public AxisAlignedBB getTargetVillageArea() {
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
    public boolean hurt(DamageSource damageSource, float amount) {
        boolean flag = super.hurt(damageSource, amount);
        if (flag && damageSource.getEntity() instanceof PlayerEntity && this.random.nextInt(4) == 0) {
            this.addEffect(new EffectInstance(ModEffects.SUNSCREEN.get(), 150, 2));
        }
        return flag;
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tagCompund) {
        super.readAdditionalSaveData(tagCompund);
        if (tagCompund.contains("level")) {
            setLevel(tagCompund.getInt("level"));
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
    public int suggestLevel(Difficulty d) {
        if (random.nextBoolean()) {
            return (int) (d.avgPercLevel * MAX_LEVEL / 100F);
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
        SupporterManager.Supporter supporter = SupporterManager.getInstance().getRandomVampire(random);
        lootBookId = supporter.bookID;
        this.getEntityData().define(LEVEL, -1);
        this.getEntityData().define(TYPE, supporter.typeId);
        this.getEntityData().define(NAME, supporter.senderName == null ? "none" : supporter.senderName);
        this.getEntityData().define(TEXTURE, supporter.textureName == null ? "none" : supporter.textureName);

    }

    @Override
    protected EntityType<?> getIMobTypeOpt(boolean iMob) {
        return iMob ? ModEntities.advanced_vampire_imob : ModEntities.advanced_vampire;
    }

    @Override
    protected int getExperienceReward(PlayerEntity player) {
        return 10 * (1 + getLevel());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new BreakDoorGoal(this, (difficulty) -> difficulty == net.minecraft.world.Difficulty.HARD));//Only break doors on hard difficulty
        this.goalSelector.addGoal(2, new RestrictSunVampireGoal<>(this));
        this.goalSelector.addGoal(3, new FleeSunVampireGoal<>(this, 0.9, false));
        this.goalSelector.addGoal(3, new FleeGarlicVampireGoal(this, 0.9, false));
        this.goalSelector.addGoal(4, new AttackMeleeNoSunGoal(this, 1.0, false));
        this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.9, 25));
        this.goalSelector.addGoal(9, new LookAtClosestVisibleGoal(this, PlayerEntity.class, 13F));
        this.goalSelector.addGoal(10, new LookAtGoal(this, HunterBaseEntity.class, 17F));
        this.goalSelector.addGoal(11, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new AttackVillageGoal<>(this));
        this.targetSelector.addGoal(2, new DefendVillageGoal<>(this));//Should automatically be mutually exclusive with  attack village
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, true, false, null)));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, CreatureEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, PatrollerEntity.class, 5, true, true, (living) -> UtilLib.isInsideStructure(living, Structure.VILLAGE)));
    }

    protected void updateEntityAttributes() {
        int l = Math.max(getLevel(), 0);
        Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(BalanceMobProps.mobProps.ADVANCED_VAMPIRE_MAX_HEALTH + BalanceMobProps.mobProps.ADVANCED_VAMPIRE_MAX_HEALTH_PL * l);
        Objects.requireNonNull(this.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(BalanceMobProps.mobProps.ADVANCED_VAMPIRE_ATTACK_DAMAGE + BalanceMobProps.mobProps.ADVANCED_VAMPIRE_ATTACK_DAMAGE_PL * l);
    }

    public static class IMob extends AdvancedVampireEntity implements net.minecraft.entity.monster.IMob {

        public IMob(EntityType<? extends AdvancedVampireEntity> type, World world) {
            super(type, world);
        }

    }
}
