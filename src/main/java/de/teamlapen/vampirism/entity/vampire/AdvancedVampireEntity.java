package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.vampire.IAdvancedVampire;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.entity.action.ActionHandlerEntity;
import de.teamlapen.vampirism.entity.ai.AttackMeleeNoSunGoal;
import de.teamlapen.vampirism.entity.ai.FleeGarlicVampireGoal;
import de.teamlapen.vampirism.entity.ai.FleeSunVampireGoal;
import de.teamlapen.vampirism.entity.ai.RestrictSunVampireGoal;
import de.teamlapen.vampirism.entity.hunter.HunterBaseEntity;
import de.teamlapen.vampirism.util.IPlayerFace;
import de.teamlapen.vampirism.util.SupporterManager;
import de.teamlapen.vampirism.world.loot.LootHandler;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

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
     * Store the approximate count of entities that are following this advanced vampire.
     * Not guaranteed to be exact and not saved to nbt
     */
    private int followingEntities = 0;
    /**
     * available actions for AI task & task
     */
    private final ActionHandlerEntity<?> entityActionHandler;
    private final EntityClassType entityclass;
    private final EntityActionTier entitytier;

    public AdvancedVampireEntity(EntityType<? extends AdvancedVampireEntity> type, World world) {
        super(type, world, true);
        this.setSize(0.6F, 1.95F);
        this.canSuckBloodFromPlayer = true;
        this.setSpawnRestriction(SpawnRestriction.SPECIAL);
        this.setDontDropEquipment();
        entitytier = EntityActionTier.High;
        entityclass = EntityClassType.getRandomClass(this.getRNG());
        IEntityActionUser.applyAttributes(this);
        this.entityActionHandler = new ActionHandlerEntity<>(this);
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float amount) {
        boolean flag = super.attackEntityFrom(damageSource, amount);
        if (flag && damageSource.getTrueSource() instanceof PlayerEntity && this.rand.nextInt(4) == 0) {
            this.addPotionEffect(new EffectInstance(ModPotions.sunscreen, 150, 2));
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
    public ITextComponent getName() {
        String senderName = this.getDataManager().get(NAME);
        return "none".equals(senderName) ? super.getName() : new StringTextComponent(senderName);
    }

    @Override
    public int getMaxFollowerCount() {
        return Balance.mobProps.ADVANCED_VAMPIRE_MAX_FOLLOWER;
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
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
    }

    @Nullable
    @Override
    public String getPlayerFaceName() {
        return getTextureName();
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
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (entityActionHandler != null) {
            entityActionHandler.handle();
        }
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.updateEntityAttributes();

    }

    @Override
    protected float calculateFireDamage(float amount) {
        return (float) (amount * Balance.mobProps.ADVANCED_VAMPIRE_FIRE_VULNERABILITY);
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
    protected int getExperiencePoints(PlayerEntity player) {
        return 10 * (1 + getLevel());
    }

    @Nullable
    @Override
    protected ResourceLocation getLootTable() {
        return LootHandler.ADVANCED_VAMPIRE;
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        if (world.getDifficulty() == net.minecraft.world.Difficulty.HARD) {
            //Only break doors on hard difficulty
            this.tasks.addTask(1, new BreakDoorGoal(this));
            ((GroundPathNavigator) this.getNavigator()).setBreakDoors(true);
        }
        this.tasks.addTask(2, new RestrictSunVampireGoal<>(this));
        this.tasks.addTask(3, new FleeSunVampireGoal<>(this, 0.9, false));
        this.tasks.addTask(3, new FleeGarlicVampireGoal(this, 0.9, false));
        this.tasks.addTask(4, new AttackMeleeNoSunGoal(this, 1.0, false));
        this.tasks.addTask(8, new RandomWalkingGoal(this, 0.9, 25));
        this.tasks.addTask(9, new LookAtGoal(this, PlayerEntity.class, 13F));
        this.tasks.addTask(10, new LookAtGoal(this, HunterBaseEntity.class, 17F));
        this.tasks.addTask(11, new LookRandomlyGoal(this));

        this.targetTasks.addTask(3, new HurtByTargetGoal(this, false));
        this.targetTasks.addTask(4, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, true, false, null)));
        this.targetTasks.addTask(5, new NearestAttackableTargetGoal<>(this, CreatureEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)));
    }

    protected void updateEntityAttributes() {
        int l = Math.max(getLevel(), 0);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Balance.mobProps.ADVANCED_VAMPIRE_MAX_HEALTH + Balance.mobProps.ADVANCED_VAMPIRE_MAX_HEALTH_PL * l);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(Balance.mobProps.ADVANCED_VAMPIRE_ATTACK_DAMAGE + Balance.mobProps.ADVANCED_VAMPIRE_ATTACK_DAMAGE_PL * l);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(Balance.mobProps.ADVANCED_VAMPIRE_SPEED);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(13);
    }

    @Override
    public EntityClassType getEntityClass() {
        return entityclass;
    }

    @Override
    public EntityActionTier getEntityTier() {
        return entitytier;
    }


}
