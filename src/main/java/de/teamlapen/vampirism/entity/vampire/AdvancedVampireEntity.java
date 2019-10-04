package de.teamlapen.vampirism.entity.vampire;

import com.mojang.authlib.GameProfile;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.vampire.IAdvancedVampire;
import de.teamlapen.vampirism.api.world.IVillageAttributes;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.entity.action.ActionHandlerEntity;
import de.teamlapen.vampirism.entity.goals.*;
import de.teamlapen.vampirism.entity.hunter.HunterBaseEntity;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import de.teamlapen.vampirism.util.IPlayerFace;
import de.teamlapen.vampirism.util.PlayerSkinHelper;
import de.teamlapen.vampirism.util.SupporterManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structures;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Advanced vampire. Is strong. Represents supporters
 */
public class AdvancedVampireEntity extends VampireBaseEntity implements IAdvancedVampire, IPlayerFace, IEntityActionUser {
    private static final DataParameter<Integer> LEVEL = EntityDataManager.createKey(AdvancedVampireEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TYPE = EntityDataManager.createKey(AdvancedVampireEntity.class, DataSerializers.VARINT);
    private static final DataParameter<String> NAME = EntityDataManager.createKey(AdvancedVampireEntity.class, DataSerializers.STRING);
    private static final DataParameter<String> TEXTURE = EntityDataManager.createKey(AdvancedVampireEntity.class, DataSerializers.STRING);

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

    @OnlyIn(Dist.CLIENT)
    @Nullable
    private GameProfile facePlayerProfile;

    public AdvancedVampireEntity(EntityType<? extends AdvancedVampireEntity> type, World world) {
        super(type, world, true);
        this.canSuckBloodFromPlayer = true;
        this.setSpawnRestriction(SpawnRestriction.SPECIAL);
        this.setDontDropEquipment();
        entitytier = EntityActionTier.High;
        entityclass = EntityClassType.getRandomClass(this.getRNG());
        IEntityActionUser.applyAttributes(this);
        this.entityActionHandler = new ActionHandlerEntity<>(this);
        this.enableImobConversion();
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float amount) {
        boolean flag = super.attackEntityFrom(damageSource, amount);
        if (flag && damageSource.getTrueSource() instanceof PlayerEntity && this.rand.nextInt(4) == 0) {
            this.addPotionEffect(new EffectInstance(ModEffects.sunscreen, 150, 2));
        }
        return flag;
    }

    @Override
    public void decreaseFollowerCount() {
        followingEntities = Math.max(0, followingEntities - 1);
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
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
        return getDataManager().get(TYPE);
    }

    @Override
    public int getFollowingCount() {
        return followingEntities;
    }

    @Override
    public int getLevel() {
        return getDataManager().get(LEVEL);
    }

    @Override
    public void setLevel(int level) {
        if (level >= 0) {
            getDataManager().set(LEVEL, level);
            this.updateEntityAttributes();
            if (level == 1) {
                this.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 1000000, 0));
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

    @Override
    public ITextComponent getName() {
        String senderName = this.getDataManager().get(NAME);
        return "none".equals(senderName) ? super.getName() : new StringTextComponent(senderName);
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    @Override
    public GameProfile getPlayerFaceProfile() {
        if (this.facePlayerProfile == null) {
            String name = getTextureName();
            if (name == null) return null;
            facePlayerProfile = new GameProfile(null, name);
            PlayerSkinHelper.updateGameProfileAsync(facePlayerProfile, (profile) -> Minecraft.getInstance().execute(() -> AdvancedVampireEntity.this.facePlayerProfile = profile));
        }
        return facePlayerProfile;
    }

    @Nullable
    public String getTextureName() {
        String texture = this.getDataManager().get(TEXTURE);
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
    public void livingTick() {
        super.livingTick();
        if (entityActionHandler != null) {
            entityActionHandler.handle();
        }
    }

    @Override
    public void read(CompoundNBT tagCompund) {
        super.read(tagCompund);
        if (tagCompund.contains("level")) {
            setLevel(tagCompund.getInt("level"));
        }
        if (tagCompund.contains("type")) {
            getDataManager().set(TYPE, tagCompund.getInt("type"));
            getDataManager().set(NAME, tagCompund.getString("name"));
            getDataManager().set(TEXTURE, tagCompund.getString("texture"));
        }
        if (entityActionHandler != null) {
            entityActionHandler.read(tagCompund);
        }
        if (tagCompund.contains("attack")) {
            this.attack = tagCompund.getBoolean("attack");
        }
        if (tagCompund.contains("x")) {
            this.villageAttributes = TotemTileEntity.getVillageAttributes((TotemTileEntity) this.world.getTileEntity(new BlockPos(tagCompund.getInt("x"), tagCompund.getInt("y"), tagCompund.getInt("z"))));
        }
    }

    @Override
    public int suggestLevel(Difficulty d) {
        if (rand.nextBoolean()) {
            return (int) (d.avgPercLevel * MAX_LEVEL / 100F);
        }
        return rand.nextInt(MAX_LEVEL + 1);

    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        nbt.putInt("level", getLevel());
        nbt.putInt("type", getEyeType());
        nbt.putString("texture", getDataManager().get(TEXTURE));
        nbt.putString("name", getDataManager().get(NAME));
        nbt.putInt("entityclasstype", EntityClassType.getID(entityclass));
        if (entityActionHandler != null) {
            entityActionHandler.write(nbt);
        }
        nbt.putBoolean("attack", this.attack);
        if (this.villageAttributes != null) {
            nbt.putInt("x", this.villageAttributes.getPosition().getX());
            nbt.putInt("y", this.villageAttributes.getPosition().getY());
            nbt.putInt("z", this.villageAttributes.getPosition().getZ());
        }
    }

    @Override
    protected float calculateFireDamage(float amount) {
        return (float) (amount * BalanceMobProps.mobProps.ADVANCED_VAMPIRE_FIRE_VULNERABILITY);
    }

    @Override
    protected int getExperiencePoints(PlayerEntity player) {
        return 10 * (1 + getLevel());
    }

    @Override
    protected EntityType<?> getIMobTypeOpt(boolean iMob) {
        return iMob ? ModEntities.advanced_vampire_imob : ModEntities.advanced_vampire;
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.updateEntityAttributes();

    }

    @Override
    protected void registerData() {
        super.registerData();
        SupporterManager.Supporter supporter = SupporterManager.getInstance().getRandomVampire(rand);
        this.getDataManager().register(LEVEL, -1);
        this.getDataManager().register(TYPE, supporter.typeId);
        this.getDataManager().register(NAME, supporter.senderName == null ? "none" : supporter.senderName);
        this.getDataManager().register(TEXTURE, supporter.textureName == null ? "none" : supporter.textureName);

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

        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<PlayerEntity>(this, PlayerEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, true, false, null)));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<CreatureEntity>(this, CreatureEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, PatrollerEntity.class, 5, true, true, (living) -> Structures.VILLAGE.isPositionInStructure(living.world, living.getPosition())));
    }

    @Override
    public ActionHandlerEntity getActionHandler() {
        return entityActionHandler;
    }

    protected void updateEntityAttributes() {
        int l = Math.max(getLevel(), 0);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(BalanceMobProps.mobProps.ADVANCED_VAMPIRE_MAX_HEALTH + BalanceMobProps.mobProps.ADVANCED_VAMPIRE_MAX_HEALTH_PL * l);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(BalanceMobProps.mobProps.ADVANCED_VAMPIRE_ATTACK_DAMAGE + BalanceMobProps.mobProps.ADVANCED_VAMPIRE_ATTACK_DAMAGE_PL * l);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(BalanceMobProps.mobProps.ADVANCED_VAMPIRE_SPEED);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(13);
    }

    public static class IMob extends AdvancedVampireEntity implements net.minecraft.entity.monster.IMob {

        public IMob(EntityType<? extends AdvancedVampireEntity> type, World world) {
            super(type, world);
        }
    }

    //Village stuff ----------------------------------------------------------------------------------------------------
    private @Nullable
    IVillageAttributes villageAttributes;
    private boolean attack;

    @Override
    public void attackVillage(IVillageAttributes totem) {
        this.villageAttributes = totem;
        this.attack = true;
    }

    @Override
    public void defendVillage(IVillageAttributes totem) {
        this.villageAttributes = totem;
        this.attack = false;
    }

    @Nullable
    @Override
    public IVillageAttributes getVillageAttributes() {
        return villageAttributes;
    }

    @Override
    public void stopVillageAttackDefense() {
        this.setCustomName(null);
        this.villageAttributes = null;
    }

    @Nullable
    @Override
    public AxisAlignedBB getTargetVillageArea() {
        return villageAttributes.getVillageArea();
    }

    @Override
    public boolean isAttackingVillage() {
        return villageAttributes != null && attack;
    }

    @Override
    public boolean isDefendingVillage() {
        return villageAttributes != null && !attack;
    }
}
